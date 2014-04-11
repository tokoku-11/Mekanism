package mekanism.client.gui;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.ListUtils;
import mekanism.api.gas.GasTank;
import mekanism.client.gui.GuiEnergyInfo.IInfoHandler;
import mekanism.client.gui.GuiFluidGauge.IFluidInfoHandler;
import mekanism.client.gui.GuiGasGauge.IGasInfoHandler;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.PacketHandler.Transmission;
import mekanism.common.inventory.container.ContainerElectrolyticSeparator;
import mekanism.common.network.PacketTileEntity;
import mekanism.common.tile.TileEntityElectrolyticSeparator;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiElectrolyticSeparator extends GuiMekanism
{
	public TileEntityElectrolyticSeparator tileEntity;

	public GuiElectrolyticSeparator(InventoryPlayer inventory, TileEntityElectrolyticSeparator tentity)
	{
		super(new ContainerElectrolyticSeparator(inventory, tentity));

		tileEntity = tentity;

		guiElements.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(Mekanism.FROM_H2*2);
				return ListUtils.asList("Using: " + multiplier + "/t", "Needed: " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png")));
		guiElements.add(new GuiFluidGauge(new IFluidInfoHandler() {
			@Override
			public FluidTank getTank()
			{
				return tileEntity.fluidTank;
			}
		}, GuiGauge.Type.STANDARD, this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png"), 5, 10));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.leftTank;
			}
		}, GuiGauge.Type.SMALL, this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png"), 58, 18));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.rightTank;
			}
		}, GuiGauge.Type.SMALL, this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png"), 100, 18));
		guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png"), 164, 15));
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		int xAxis = (x - (width - xSize) / 2);
		int yAxis = (y - (height - ySize) / 2);

		if(xAxis > 8 && xAxis < 17 && yAxis > 73 && yAxis < 82)
		{
			ArrayList data = new ArrayList();
			data.add((byte)0);

			PacketHandler.sendPacket(Transmission.SERVER, new PacketTileEntity().setParams(Coord4D.get(tileEntity), data));
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);

		}
		else if(xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82)
		{
			ArrayList data = new ArrayList();
			data.add((byte)1);

			PacketHandler.sendPacket(Transmission.SERVER, new PacketTileEntity().setParams(Coord4D.get(tileEntity), data));
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);

		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(tileEntity.getInvName(), 45, 6, 0x404040);

		String name = tileEntity.dumpLeft ? "Dumping..." : tileEntity.leftTank.getGas() == null ? MekanismUtils.localize("gui.none") : tileEntity.leftTank.getGas().getGas().getLocalizedName();
		fontRenderer.drawString(name, 21, 73, 0x404040);

		name = tileEntity.dumpRight ? "Dumping..." : tileEntity.rightTank.getGas() == null ? MekanismUtils.localize("gui.none") : tileEntity.rightTank.getGas().getGas().getLocalizedName();
		fontRenderer.drawString(name, 156-fontRenderer.getStringWidth(name), 73, 0x404040);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiElectrolyticSeparator.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int displayInt = tileEntity.dumpLeft ? 60 : 52;
		drawTexturedModalRect(guiWidth + 8, guiHeight + 73, 176, displayInt, 8, 8);

		displayInt = tileEntity.dumpRight ? 60 : 52;
		drawTexturedModalRect(guiWidth + 160, guiHeight + 73, 176, displayInt, 8, 8);

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
}