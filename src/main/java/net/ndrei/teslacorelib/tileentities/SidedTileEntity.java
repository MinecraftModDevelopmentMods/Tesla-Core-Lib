package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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
import net.ndrei.teslacorelib.items.BaseAddon;
import net.ndrei.teslacorelib.items.TeslaWrench;
import net.ndrei.teslacorelib.netsync.ISimpleNBTMessageHandler;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2017-03-09.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class SidedTileEntity extends TileEntity implements
        ITickable, IHudInfoProvider, ISimpleNBTMessageHandler, IGuiContainerProvider, ITeslaWrenchHandler {
    private static final int SYNC_ON_TICK = 20;
    private int syncTick = SYNC_ON_TICK;

    private SidedItemHandler itemHandler;
    private List<SidedTileEntity.InventoryStorageInfo> inventoryStorage;

    protected SidedFluidHandler fluidHandler;
    private ItemStackHandler fluidItems = null;

    protected ItemStackHandler addonItems;

    private int typeId; // used for message sync

    protected SidedItemHandlerConfig sideConfig;

    @SuppressWarnings("unused")
    protected SidedTileEntity(int typeId) {
        this.typeId = typeId;
        this.sideConfig = new SidedItemHandlerConfig() {
            @Override
            protected void updated() {
                SidedTileEntity.this.notifyNeighbours();
            }
        };
        this.itemHandler = new SidedItemHandler(this.sideConfig);
        this.fluidHandler = new SidedFluidHandler(this.sideConfig);

        this.initializeInventories();
        this.ensureFluidItems();
    }

    @SuppressWarnings("ConstantConditions")
    private void notifyNeighbours() {
        if (this.getWorld() != null) {
            this.getWorld().notifyNeighborsOfStateChange(
                    this.getPos(),
                    this.getBlockType(),
                    true
            );
        }
    }

    //region inventory         methods

    protected void initializeInventories() {
        this.createAddonsInventory();
    }

    protected void createAddonsInventory() {
        this.addonItems = new ItemStackHandler(4) {
            private ItemStack[] items = new ItemStack[] { null, null, null, null };

            @Override
            protected void onContentsChanged(int slot) {
                this.testSlot(slot);
            }

            @Override
            protected void onLoad() {
                for(int index = 0; index < this.getSlots(); index++) {
                    this.testSlot(index);
                }
            }

            private void testSlot(int slot) {
                ItemStack stack = this.getStackInSlot(slot);
                Item item = (ItemStackUtil.isEmpty(stack) ? null : stack.getItem());
                if (!(item instanceof BaseAddon)) {
                    item = null;
                }

                if ((item == null) && (this.items[slot] != null)) {
                    ((BaseAddon)this.items[slot].getItem()).onRemoved(this.items[slot], SidedTileEntity.this);
                    this.items[slot] = null;
                }
                else if ((item != null) && (this.items[slot] == null)) {
                    ((BaseAddon) item).onAdded(this.items[slot] = stack, SidedTileEntity.this);
                }
                else if ((item != null) && (this.items[slot] != null) && !ItemStack.areItemStacksEqual(this.items[slot], stack)) {
                    ((BaseAddon)this.items[slot].getItem()).onRemoved(this.items[slot], SidedTileEntity.this);
                    ((BaseAddon) item).onAdded(this.items[slot] = stack, SidedTileEntity.this);
                }
                SidedTileEntity.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(this.addonItems, null, null, null) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return SidedTileEntity.this.isValidAddonItem(stack);
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

    @SuppressWarnings("unchecked")
    protected <T extends BaseAddon> T getAddon(Class<T> addonClass) {
        if ((this.addonItems != null) && (addonClass != null)) {
            for (int index = 0; index < this.addonItems.getSlots(); index++) {
                ItemStack stack = this.addonItems.getStackInSlot(index);
                if (!ItemStackUtil.isEmpty(stack)) {
                    Item item = stack.getItem();
                    if (addonClass.isAssignableFrom(item.getClass())) {
                        return (T)item;
                    }
                }
            }
        }
        return null;
    }

    protected <T extends BaseAddon> ItemStack getAddonStack(Class<T> addonClass) {
        if ((this.addonItems != null) && (addonClass != null)) {
            for (int index = 0; index < this.addonItems.getSlots(); index++) {
                ItemStack stack = this.addonItems.getStackInSlot(index);
                if (!ItemStackUtil.isEmpty(stack)) {
                    if (addonClass.isAssignableFrom(stack.getItem().getClass())) {
                        return stack;
                    }
                }
            }
        }
        return ItemStackUtil.getEmptyStack();
    }

    protected <T extends BaseAddon> boolean hasAddon(Class<T> addonClass) {
        return (null != this.getAddon(addonClass));
    }

    protected List<BaseAddon> getAddons() {
        List<BaseAddon> list = Lists.newArrayList();

        if (this.addonItems != null) {
            for (int index = 0; index < this.addonItems.getSlots(); index++) {
                ItemStack stack = this.addonItems.getStackInSlot(index);
                if (!ItemStackUtil.isEmpty(stack)) {
                    Item item = stack.getItem();
                    if (item instanceof BaseAddon) {
                        list.add((BaseAddon) item);
                    }
                }
            }
        }

        return list;
    }

    protected boolean isValidAddonItem(ItemStack stack) {
        if (!ItemStackUtil.isEmpty(stack)) {
            Item item = stack.getItem();
            if (item instanceof BaseAddon) {
                BaseAddon addon = (BaseAddon)item;
                if (addon.canBeAddedTo(this)) {
                    return true;
                }
            }
        }
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

    protected void addInventoryToStorage(ItemStackHandler handler, String storageKey) {
        if (this.inventoryStorage == null) {
            this.inventoryStorage = Lists.newArrayList();
        }

        this.inventoryStorage.add(new SidedTileEntity.InventoryStorageInfo(handler, storageKey));
    }

    private final class InventoryStorageInfo {
        final ItemStackHandler inventory;
        final String storageKey;

        InventoryStorageInfo(ItemStackHandler inventory, String storageKey) {
            this.inventory = inventory;
            this.storageKey = storageKey;
        }
    }

    //endregion

    //region fluid tank        methods

    @SuppressWarnings("SameParameterValue")
    protected IFluidTank addFluidTank(Fluid filter, int capacity, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        FluidTank tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged()
            {
                SidedTileEntity.this.markDirty();
            }
        };
        ColoredFluidHandler colored = this.fluidHandler.addTank(filter, tank, color, name, boundingBox);

        if ((color != null) && (name != null) && (name.length() > 0) && (boundingBox != null)) {
            this.sideConfig.addColoredInfo(name, color, boundingBox);
        }

        return colored.getInnerTank();
    }

    protected void addFluidTank(IFluidTank tank, BoundingRectangle box) {
        if (tank == null) {
            return;
        }
        this.fluidHandler.addTank(tank);

        if (tank instanceof ColoredFluidHandler) {
            ColoredFluidHandler colored = (ColoredFluidHandler)tank;
            if (box == null) {
                box = colored.getBoundingBox();
            }
            if ((box != null) && (colored.getColor() != null)) {
                this.sideConfig.addColoredInfo(colored.getName(), colored.getColor(), box);
            }
        }
    }

    protected void addFluidTank(IFluidTank tank, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        if ((color != null) && (name != null) && (name.length() > 0) && (boundingBox != null)) {
            this.fluidHandler.addTank(new ColoredFluidHandler(tank, color, name, boundingBox));
            this.sideConfig.addColoredInfo(name, color, boundingBox);
        }
        else {
            this.fluidHandler.addTank(tank);
        }
    }

    protected void removeFluidTank(EnumDyeColor color, IFluidTank tank) {
        this.fluidHandler.removeTank(tank);
        this.sideConfig.removeColoredInfo(color);
        this.markDirty();
    }

    protected EnumDyeColor getColorForFluidInventory() {
        return EnumDyeColor.SILVER;
    }

    protected void ensureFluidItems() {
        if (!shouldAddFluidItemsInventory())
            return;

        if (this.fluidItems == null) {
            EnumDyeColor color = this.getColorForFluidInventory();
            if (color != null) {
                BoundingRectangle box = this.getFluidItemsBoundingBox();

                this.fluidItems = new ItemStackHandler(2) {
//                    @Override
//                    public final int getSlotLimit(int slot) {
//                        if (slot == 0) {
//                            return 1;
//                        }
//                        ItemStack stack = this.getStackInSlot(slot);
//                        if (!ItemStackUtil.isEmpty(stack)) {
//                            return stack.getMaxStackSize();
//                        }
//                        return 64; // ??
//                    }
                };
                this.addInventory(new ColoredItemHandler(this.fluidItems, color, "Fluid Containers", box) {
                    @Override
                    public boolean canInsertItem(int slot, ItemStack stack) {
                        return (slot == 0) && SidedTileEntity.this.acceptsFluidItem(stack);
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
                            SidedTileEntity.this.addFluidItemsBackground(pieces, box);
                        }

                        return pieces;
                    }
                });
            }
        }
    }

    protected boolean acceptsFluidItem(ItemStack stack) {
        return (this.fluidHandler != null) && this.fluidHandler.acceptsFluidFrom(stack);
    }

    protected BoundingRectangle getFluidItemsBoundingBox() {
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
        return new BoundingRectangle(x, y, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT);
    }

    protected void addFluidItemsBackground(List<IGuiContainerPiece> pieces, BoundingRectangle box) {
        pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 54,
                BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189));
    }

    protected boolean shouldAddFluidItemsInventory() {
        return !((this.fluidHandler == null) || (this.fluidHandler.tankCount() == 0));
    }

    //endregion

    public EnumFacing getFacing() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        if (state.getBlock() instanceof OrientedBlock) {
            return state.getValue(OrientedBlock.FACING);
        }
        return EnumFacing.NORTH;
    }

    //region storage & sync    methods

    protected int getEntityTypeId() {
        return this.typeId;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("addonItems") && (this.addonItems != null)) {
            this.addonItems.deserializeNBT(compound.getCompoundTag("addonItems"));
        }

        if (compound.hasKey("fluids")) {
            this.fluidHandler.deserializeNBT(compound.getCompoundTag("fluids"));
        }
        if (compound.hasKey("fluidItems") && (this.fluidItems != null)) {
            this.fluidItems.deserializeNBT(compound.getCompoundTag("fluidItems"));
        }

        this.syncTick = compound.getInteger("tick_sync");

        if (compound.hasKey("side_config", Constants.NBT.TAG_LIST)) {
            NBTTagList list = compound.getTagList("side_config", Constants.NBT.TAG_COMPOUND);
            this.sideConfig.deserializeNBT(list);
        }

        if ((this.inventoryStorage != null) && !this.inventoryStorage.isEmpty()) {
            for(SidedTileEntity.InventoryStorageInfo storage : this.inventoryStorage) {
                if (compound.hasKey(storage.storageKey, Constants.NBT.TAG_COMPOUND)) {
                    storage.inventory.deserializeNBT(compound.getCompoundTag(storage.storageKey));
                }
            }
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setTag("fluids", this.fluidHandler.serializeNBT());
        if (this.fluidItems != null) {
            compound.setTag("fluidItems", this.fluidItems.serializeNBT());
        }
        if (this.addonItems != null) {
            compound.setTag("addonItems", this.addonItems.serializeNBT());
        }

        compound.setInteger("tick_sync", this.syncTick);

        compound.setTag("side_config", this.sideConfig.serializeNBT());

        if ((this.inventoryStorage != null) && !this.inventoryStorage.isEmpty()) {
            for(SidedTileEntity.InventoryStorageInfo storage : this.inventoryStorage) {
                compound.setTag(storage.storageKey, storage.inventory.serializeNBT());
            }
        }

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
        return Lists.newArrayList();
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
            // TeslaCoreLib.logger.info("WRENCH!!");
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
        this.innerUpdate();

        this.processImmediateInventories();

        if (!this.getWorld().isRemote) {
            this.syncTick++;
            if (this.syncTick >= SYNC_ON_TICK) {
                TeslaCoreLib.network.send(new SimpleNBTMessage(this, this.writeToNBT()));
                this.syncTick = 0;
            }
        }
    }

    protected abstract void innerUpdate();

    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    protected void forceSync() {
        if ((this.getWorld() != null) && !this.getWorld().isRemote) {
            this.syncTick = SYNC_ON_TICK;
        }
    }

    protected void processImmediateInventories() {
        if (this.fluidItems != null) {
            this.processFluidItems(this.fluidItems);
        }
    }

    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStack stack = fluidItems.getStackInSlot(0);
        if (!ItemStackUtil.isEmpty(stack) && this.fluidHandler.acceptsFluidFrom(stack)) {
            ItemStack result = this.fluidHandler.fillFluidFrom(stack);
            if (!ItemStack.areItemStacksEqual(stack, result)) {
                fluidItems.setStackInSlot(0, result);
                this.discardUsedFluidItem();
            }
        } else if (!ItemStackUtil.isEmpty(stack)) {
            this.discardUsedFluidItem();
        }
    }

    protected void discardUsedFluidItem() {
        if (this.fluidItems != null) {
            ItemStack source = this.fluidItems.getStackInSlot(0);
            ItemStack result = this.fluidItems.insertItem(1, source, false);
            this.fluidItems.setStackInSlot(0, result);
        }
    }

    public void onBlockBroken() {
        if (this.itemHandler != null) {
            for (int i = 0; i < this.itemHandler.getSlots(); ++i) {
                ItemStack stack = this.itemHandler.getStackInSlot(i);
                if (!ItemStackUtil.isEmpty(stack)) {
                    InventoryHelper.spawnItemStack(this.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }
}
