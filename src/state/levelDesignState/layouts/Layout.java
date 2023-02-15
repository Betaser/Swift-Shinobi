package state.levelDesignState.layouts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import base.Game;
import button.Button;
import button.ButtonGroup;
import helper.Calc;
import levels.Level;
import math.Vec;

public class Layout {
	
	public String name;
	
	double depth;
	boolean visible = true;
	ButtonGroup buttonGroup;
	Button selectBtn;
	Button visibilityBtn;
	Button renameBtn;

	public Level level;
	public double defEnDepth;
	
	public Layout(String name, double depth, ButtonGroup buttonGroup) {
		this.name = name;
		this.depth = depth;
		level = new Level();
		this.buttonGroup = buttonGroup;
	}
	
	public void groupBtns() {
		buttonGroup.buttons.addAll(List.of(
			visibilityBtn,
			renameBtn,
			selectBtn
		));
	}
	
	public void tick(boolean cursorPress) {

	}
	
	public void render(ArrayList<Runnable> renderers) {
		renderers.add(() -> {
			Graphics g = Game.get().getGraphics(depth);
			g.setFont(new Font("Courier New", Font.PLAIN, 20));
			Vec selectBtnPos = selectBtn.bounds.pos();
			Color purple = Color.MAGENTA;
			purple = new Color(purple.getRed() - 200, Math.max(0, purple.getGreen() - 100), purple.getBlue() - 200);
			g.setColor(purple);
			final int HEIGHT = Calc.containingRect(selectBtn.bounds).height;
			
			//	Draw purple background
			g.fillRect(
				selectBtnPos.getXInt() - name.length() * 12, 
				Game.get().getHeight() - (HEIGHT + selectBtnPos.getYInt()), 
				name.length() * 12, 
				HEIGHT
			);
			
			//	Draw layout name
			g.setColor(Color.YELLOW);
			g.drawString(
				name, 
				selectBtnPos.getXInt() - name.length() * 12, 
				Game.get().getHeight() - (HEIGHT / 2 + selectBtnPos.getYInt())
			);			

			//	Draw layer's depth
			g.setColor(Color.WHITE);
			final String SEE_DEPTH = defEnDepth % 1 == 0 ? "" + (int) defEnDepth : "" + defEnDepth;
			g.drawString(
				SEE_DEPTH, 
				selectBtnPos.getXInt() - SEE_DEPTH.length() * 12, 
				Game.get().getHeight() - (HEIGHT / 10 + selectBtnPos.getYInt())
			);
		});
		
		if (visible)
			level.entities.forEach(e -> e.render(Game.get().getGraphics(e.depth)));
	}

}
