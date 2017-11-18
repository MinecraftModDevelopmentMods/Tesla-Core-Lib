package net.modcrafters.mclib.ingredients

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.modcrafters.mclib.ingredients.implementations.EnergyWrapperIngredient
import net.modcrafters.mclib.ingredients.implementations.FluidWrapperIngredient
import net.modcrafters.mclib.ingredients.implementations.ItemWrapperIngredient

fun FluidStack.asIngredient() = FluidWrapperIngredient(this)

fun ItemStack.asIngredient() = if (this.isEmpty) null else ItemWrapperIngredient(this)

fun IGenericEnergyStorage.asIngredient() = EnergyWrapperIngredient(this)