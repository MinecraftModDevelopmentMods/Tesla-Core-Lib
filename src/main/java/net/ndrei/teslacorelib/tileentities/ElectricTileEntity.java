package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.blocks.OrientedBlock;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.capabilities.container.IGuiContainerProvider;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.capabilities.hud.IHudInfoProvider;
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.*;
import net.ndrei.teslacorelib.items.TeslaWrench;
import net.ndrei.teslacorelib.netsync.ISimpleNBTMessageHandler;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
public abstract class ElectricTileEntity extends TileEntity implements
        ITickable, IHudInfoProvider, ISimpleNBTMessageHandler, IGuiContainerProvider, ITeslaWrenchHandler {
    private static final int SYNC_ON_TICK = 20;
    private int syncTick = SYNC_ON_TICK;

    @SuppressWarnings("WeakerAccess")
    protected EnergyStorage energyStorage;
    private SidedItemHandler itemHandler;

    private SidedFluidHandler fluidHandler;
    private ItemStackHandler fluidItems = null;

    protected ItemStackHandler addonItems;

    private int typeId; // used for message sync

    protected SidedItemHandlerConfig sideConfig;

    @SuppressWarnings("unused")
    protected ElectricTileEntity(int typeId) {
        this.typeId = typeId;
        this.sideConfig = new SidedItemHandlerConfig();
        this.itemHandler = new SidedItemHandler(this.sideConfig);
        this.fluidHandler = new SidedFluidHandler(this.sideConfig);

        this.energyStorage = new EnergyStorage(this.getMaxEnergy(), this.getEnergyInputRate(), this.getEnergyOutputRate()) {
            @Override
            public void onChanged() {
                ElectricTileEntity.this.markDirty();
                ElectricTileEntity.this.forceSync();
            }
        };

        this.initializeInventories();
        this.ensureFluidItems();
    }

    //region inventory         methods

    protected void initializeInventories() {
        this.energyStorage.setSidedConfig(EnumDyeColor.LIGHT_BLUE, this.sideConfig, this.getEnergyBoundingBox());
        this.createAddonsInventory();
    }

    protected void createAddonsInventory() {
        this.addonItems = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                ElectricTileEntity.this.markDirty();
            }

            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(this.addonItems, null, null, null) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ElectricTileEntity.this.isValidAddonItem(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                for(int y = 0; y < 4; y++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), y, 174, 8 + y * 18));
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                pieces.add(new TiledRenderedGuiPiece(173, 7, 18, 18, 1, 4,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 144, 190, null));

                return pieces;
            }
        });
    }

    protected boolean isValidAddonItem(ItemStack stack) {
        return false;
    }

    protected void addInventory(IItemHandler handler) {
        if (handler == null) {
            return;
        }
        this.itemHandler.addItemHandler(handler);

        if ((handler instanceof ColoredItemHandler)) {
            ColoredItemHandler colored = (ColoredItemHandler)handler;
            if ((colored.getColor() != null) && (colored.getBoundingBox() != null)) {
                this.sideConfig.addColoredInfo(colored.getName(), colored.getColor(), colored.getBoundingBox());
            }
        }
    }

    protected BoundingRectangle getEnergyBoundingBox() {
        return new BoundingRectangle(7, 25, 18, 54);
    }

    //endregion

    //region fluid tank        methods

    protected IFluidTank addFluidTank(Fluid filter, int capacity, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        ColoredFluidHandler tank = this.fluidHandler.addTank(filter, capacity, color, name, boundingBox);

        if ((color != null) && (name != null) && (name.length() > 0) && (boundingBox != null)) {
            this.sideConfig.addColoredInfo(name, color, boundingBox);
        }

        return tank.getInnerTank();
    }

    protected void addFluidTank(IFluidTank tank, BoundingRectangle box) {
        if (tank == null) {
            return;
        }
        this.fluidHandler.addTank(tank);

        if ((tank instanceof ColoredFluidHandler) && (box != null)) {
            ColoredFluidHandler colored  = (ColoredFluidHandler)tank;
            this.sideConfig.addColoredInfo(colored.getName(), colored.getColor(), box);
        }
    }


    protected EnumDyeColor getColorForFluidInventory() {
        return EnumDyeColor.SILVER;
    }

    protected void ensureFluidItems() {
        if ((this.fluidHandler == null) || (this.fluidHandler.tankCount() == 0)) {
            return;
        }

        if (this.fluidItems == null) {
            EnumDyeColor color = this.getColorForFluidInventory();
            if (color != null) {
                int x = 0, y = 0;
                for(IFluidTank tank : this.fluidHandler.getTanks()) {
                    if (tank instanceof ColoredFluidHandler) {
                        BoundingRectangle box = ((ColoredFluidHandler) tank).getBoundingBox();
                        if (box != null) {
                            x = Math.max(x, box.getRight());
                            y = box.getTop();
                        }
                    }
                }

                this.fluidItems = new ItemStackHandler(2);
                this.addInventory(new ColoredItemHandler(this.fluidItems, color, "Fluid Containers", new BoundingRectangle(x, y, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
                    @Override
                    public boolean canInsertItem(int slot, ItemStack stack) {
                        return (slot == 0) && ElectricTileEntity.this.fluidHandler.acceptsFluidFrom(stack);
                    }

                    @Override
                    public boolean canExtractItem(int slot) {
                        return (slot != 0);
                    }

                    @Override
                    public List<Slot> getSlots(BasicTeslaContainer container) {
                        List<Slot> slots = super.getSlots(container);

                        BoundingRectangle box = this.getBoundingBox();
                        if (box != null) {
                            slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));
                            slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 1, box.getLeft() + 1, box.getTop() + 1 + 36));
                        }

                        return slots;
                    }

                    @Override
                    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                        BoundingRectangle box = this.getBoundingBox();
                        if (box != null) {
                            pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 54,
                                    BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189));
                        }

                        return pieces;
                    }
                });
            }
        }
    }

    //endregion

    protected EnumFacing getFacing() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        if (state.getBlock() instanceof OrientedBlock) {
            return state.getValue(OrientedBlock.FACING);
        }
        return EnumFacing.NORTH;
    }

    //region energy            methods

    @SuppressWarnings("WeakerAccess")
    protected long getMaxEnergy() {
        return 50000;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyInputRate() {
        return 80;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyOutputRate() {
        return 0;
    }

    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    protected void forceSync() {
        if ((this.getWorld() != null) && !this.getWorld().isRemote) {
            this.syncTick = SYNC_ON_TICK;
        }
    }

    //endregion

    //region storage & sync    methods

    @SuppressWarnings("WeakerAccess")
    protected int getEntityTypeId() {
        return this.typeId;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("energy")) {
            this.energyStorage.deserializeNBT(compound.getCompoundTag("energy"));
        }
        if (compound.hasKey("fluids")) {
            this.fluidHandler.deserializeNBT(compound.getCompoundTag("fluids"));
        }
        if (compound.hasKey("fluidItems") && (this.fluidItems != null)) {
            this.fluidItems.deserializeNBT(compound.getCompoundTag("fluidItems"));
        }
        if (compound.hasKey("addonItems") && (this.addonItems != null)) {
            this.addonItems.deserializeNBT(compound.getCompoundTag("addonItems"));
        }

        this.syncTick = compound.getInteger("tick_sync");

        if (compound.hasKey("side_config", Constants.NBT.TAG_LIST)) {
            NBTTagList list = compound.getTagList("side_config", Constants.NBT.TAG_COMPOUND);
            this.sideConfig.deserializeNBT(list);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setTag("energy", this.energyStorage.serializeNBT());
        compound.setTag("fluids", this.fluidHandler.serializeNBT());
        if (this.fluidItems != null) {
            compound.setTag("fluidItems", this.fluidItems.serializeNBT());
        }
        if (this.addonItems != null) {
            compound.setTag("addonItems", this.addonItems.serializeNBT());
        }

        compound.setInteger("tick_sync", this.syncTick);

        compound.setTag("side_config", this.sideConfig.serializeNBT());

        return compound;
    }

    private NBTTagCompound writeToNBT() {
        NBTTagCompound compound = this.setupSpecialNBTMessage(null);
        return this.writeToNBT(compound);
    }

    public NBTTagCompound setupSpecialNBTMessage(String messageType) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("__tetId", this.getEntityTypeId());
        if ((messageType != null) && (messageType.length() > 0)) {
            compound.setString("__messageType", messageType);
        }
        return compound;
    }

    @Override
    public SimpleNBTMessage handleMessage(SimpleNBTMessage message) {
        NBTTagCompound compound = (message == null) ? null : message.getCompound();
        if (compound != null) {
            int tetId = compound.getInteger("__tetId");
            if (tetId == this.getEntityTypeId()) {
                if (compound.hasKey("__messageType", Constants.NBT.TAG_STRING)) {
                    String messageType = compound.getString("__messageType");
                    if (this.getWorld().isRemote) {
                        return this.processServerMessage(messageType, compound);
                    } else {
                        return this.processClientMessage(messageType, compound);
                    }
                } else if (this.getWorld().isRemote) {
                    this.processServerMessage(compound);
                }
            }
            else {
                TeslaCoreLib.logger.info("Unknown message for __tetId: " + tetId + " : " + compound.toString());
            }
        }
        return null;
    }

    protected void processServerMessage(NBTTagCompound compound) {
        this.readFromNBT(compound);
    }

    protected SimpleNBTMessage processServerMessage(String messageType, NBTTagCompound compound) { return null; }
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        if ((messageType != null) && messageType.equals("TOGGLE_SIDE")) {
            EnumDyeColor color = EnumDyeColor.byMetadata(compound.getInteger("color"));
            EnumFacing facing = EnumFacing.getFront(compound.getInteger("side"));
//            TeslaCoreLib.logger.info("Processing message " + messageType + " on server: " + color + " " + facing);
            this.sideConfig.toggleSide(color, facing);
            this.markDirty();
        }

        return null;
    }

    //endregion

    //region capability & info methods

    protected EnumFacing orientFacing(EnumFacing facing) {
        if (facing == null) {
            return null;
        }

        if ((facing == EnumFacing.UP) || (facing == EnumFacing.DOWN)) {
            return facing;
        }

        EnumFacing machineFacing = this.getFacing();
        if (machineFacing == EnumFacing.EAST) {
            return facing.rotateY();
        }
        if (machineFacing == EnumFacing.NORTH) {
            return facing.getOpposite(); // .rotateY().rotateY();
        }
        if (machineFacing == EnumFacing.WEST) {
            return facing.rotateYCCW();
        }
        return facing;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        facing = this.orientFacing(facing);

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            int[] slots = this.itemHandler.getSlotsForFace(facing);
            return ((slots != null) && (slots.length > 0));
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_HUD_INFO) {
            return true;
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER) {
            return true;
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_WRENCH) {
            return true;
        } else if ((this.energyStorage != null) && this.energyStorage.hasCapability(capability, facing)) {
            return true;
        } else if ((this.fluidHandler != null) && this.fluidHandler.hasCapability(capability, facing)) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        facing = this.orientFacing(facing);

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)this.itemHandler.getSideWrapper(facing);
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_HUD_INFO) {
            return (T) this;
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER) {
            return (T) this;
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_WRENCH) {
            return (T) this;
        }

        if (this.energyStorage != null) {
            T c = this.energyStorage.getCapability(capability, facing);
            if (c != null) {
                return c;
            }
        }

        if (this.fluidHandler != null) {
            T c = this.fluidHandler.getCapability(capability, facing);
            if (c != null) {
                return c;
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public List<HudInfoLine> getHUDLines() {
        List<HudInfoLine> list = Lists.newArrayList();
        return list;
    }

    @Override
    public EnumActionResult onWrenchUse(TeslaWrench wrench,
                                 EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                 EnumFacing facing, float hitX, float hitY, float hitZ) {
        if ((world == null) || !world.equals(this.getWorld()) || (pos == null) || !pos.equals(this.getPos())) {
            return EnumActionResult.PASS;
        }

        if (!this.getWorld().isRemote) {
            // client side
            TeslaCoreLib.logger.info("WRENCH!!");
            if (this.getBlockType() instanceof OrientedBlock) {
                return this.getBlockType().rotateBlock(this.getWorld(), this.getPos(), EnumFacing.UP)
                        ? EnumActionResult.SUCCESS
                        : EnumActionResult.PASS;
            }
        }
//        else  {
//            // server side
//            TeslaCoreLib.logger.info("WRENCH!!");
//        }

        return EnumActionResult.PASS;
    }

    //endregion

    //#region gui / containers  nethods

    @Override
    public BasicTeslaContainer getContainer(int id, EntityPlayer player) {
        return new BasicTeslaContainer<>(this, player);
    }

    @Override
    public BasicTeslaGuiContainer getGuiContainer(int id, EntityPlayer player) {
        return new BasicTeslaGuiContainer<>(id, this.getContainer(id, player), this);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = Lists.newArrayList();

        pieces.add(new MachineNameGuiPiece(this.getBlockType().getUnlocalizedName() + ".name",
                7, 7, 162, 12));

        BoundingRectangle energyBox = this.getEnergyBoundingBox();
        if (energyBox != null) {
            pieces.add(new TeslaEnergyLevelPiece(energyBox.getLeft(), energyBox.getTop(), this.energyStorage));
        }

        pieces.add(new PlayerInventoryBackground(7, 101, 162, 54));
        SideConfigurator configurator = new SideConfigurator(7, 101, 162, 54, this.sideConfig, this);
        pieces.add(configurator);
        pieces.add(new SideConfigSelector(7, 81, 162, 18, this.sideConfig, configurator));

        for(int i = 0; i < this.itemHandler.getInventories(); i++) {
            IItemHandler handler = this.itemHandler.getInventory(i);
            if(handler instanceof IGuiContainerPiecesProvider) {
                List<IGuiContainerPiece> childPieces = ((IGuiContainerPiecesProvider)handler).getGuiContainerPieces(container);
                if ((childPieces != null) && (childPieces.size() > 0)) {
                    pieces.addAll(childPieces);
                }
            }
        }

        List<IGuiContainerPiece> fluidPieces = this.fluidHandler.getGuiContainerPieces(container);
        if ((fluidPieces != null) && (fluidPieces.size() > 0)) {
            pieces.addAll(fluidPieces);
        }

        return pieces;
    }

    @Override
    public List<Slot> getSlots(BasicTeslaContainer container) {
        List<Slot> slots = Lists.newArrayList();

        for(int i = 0; i < this.itemHandler.getInventories(); i++) {
            IItemHandler handler = this.itemHandler.getInventory(i);
            if(handler instanceof IContainerSlotsProvider) {
                List<Slot> childSlots = ((IContainerSlotsProvider)handler).getSlots(container);
                if ((childSlots != null) && (childSlots.size() > 0)) {
                    slots.addAll(childSlots);
                }
            }
        }

        return slots;
    }

    //#endregion

    @Override
    public final void update() {
        this.protectedUpdate();
        this.energyStorage.processStatistics();

        this.processImmediateInventories();

        if (!this.getWorld().isRemote) {
            this.syncTick++;
            if (this.syncTick >= SYNC_ON_TICK) {
                TeslaCoreLib.network.send(new SimpleNBTMessage(this, this.writeToNBT()));
                this.syncTick = 0;
            }
        }
    }

    protected void processImmediateInventories() {
        if (this.fluidItems != null) {
            ItemStack stack = this.fluidItems.getStackInSlot(0);
            if (!ItemStackUtil.isEmpty(stack) && this.fluidHandler.acceptsFluidFrom(stack)) {
                ItemStack result = this.fluidHandler.fillFluidFrom(stack);
                if (!ItemStack.areItemStacksEqual(stack, result)) {
                    this.fluidItems.setStackInSlot(0, result);
                    this.discardUsedFluidItem();
                }
            } else if (!ItemStackUtil.isEmpty(stack)) {
                this.discardUsedFluidItem();
            }
        }
    }

    private void discardUsedFluidItem() {
        if (this.fluidItems != null) {
            ItemStack source = this.fluidItems.getStackInSlot(0);
            ItemStack result = this.fluidItems.insertItem(1, source, false);
            this.fluidItems.setStackInSlot(0, result);
        }
    }

    protected void protectedUpdate() { }
}
