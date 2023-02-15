package state.levelDesignState;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import base.Game;
import button.Button;
import button.ButtonGroup;
import entity.Entity;
import helper.Calc;
import helper.Utils;
import math.Polygon;
import math.Sizer;
import math.Vec;
import ui.Controllable;

public class ChooseEntitywork implements Controllable {
	
	static class ChooseEntityButton extends Button {

		Entity entity;
		static final double WIDTH = Entity.DEF_WIDTH * 1.2;
		static final double HEIGHT = Entity.DEF_HEIGHT * 1.2;
		
		public ChooseEntityButton(Polygon bounds) {
			this.bounds = bounds;
		}
		
		public void init(Entity entity, Vec selectorPos, Runnable onPress, Sizer cam) {
			this.entity = entity;
			this.selectorPos = selectorPos;
			this.onPress = onPress;
			this.cam = cam;
			render = this::render;
		}
		
		void moveEntity(Entity entity) {
			final Rectangle RECT = Calc.containingRect(bounds);
			final Rectangle EN_RECT = Calc.containingRect(entity.bounds);
			Vec center = bounds.pos().clone();
			center.addX((RECT.width - EN_RECT.width) / 2);
			center.addY((RECT.height - EN_RECT.height) / 2);
			entity.bounds.moveTo(center);
		}
		
		void resizeEntity(Entity entity) {
			entity.bounds.set(Utils.mkCenteredRect(new Vec(), Entity.DEF_WIDTH / 2, Entity.DEF_HEIGHT / 2));
		}
		
		public void renderNormal(Graphics g) {
			bounds.drawFilled(g, Utils.diffAlpha(Color.YELLOW, 100), cam);
		}
		
		public void render(Graphics g) {
			super.render(g);
			entity.render.accept(g);
		}
	
	}
	
	public ButtonGroup chooseEntityBar;

	Vec cursor;
	boolean select;
	Rectangle PANEL_RECT;
	Brushwork brushwork;
	Camwork camwork;
	LevelDesignState LDState;
	
	public void init(Vec cursor, Rectangle PANEL_RECT, Brushwork brushwork, Camwork camwork, LevelDesignState LDState) {
		this.cursor = cursor;
		this.PANEL_RECT = PANEL_RECT;
		this.brushwork = brushwork;
		this.camwork = camwork;
		this.LDState = LDState;
		chooseEntityBar = new ButtonGroup(initButtons());
		select = false;
	}
	
	public List<Button> initButtons() {
		List<Button> buttons = new ArrayList<>();
		List<Entity> entities = Entity.entities();
		for (int i = 0; i < entities.size(); i++) {
			Entity en = entities.get(i);

			ChooseEntityButton chooseBtn = new ChooseEntityButton(initBounds(i, entities));
			chooseBtn.resizeEntity(en);
			chooseBtn.moveEntity(en);

			Entity newEn = en.clone();
			newEn.init();

			Runnable onPress = () -> {
				brushwork.setBrush(List.of(newEn), camwork);
				LDState.tool = LevelDesignState.Tool.BRUSH;
			};

			chooseBtn.init(en, cursor, onPress, Sizer.DEF);
			
			buttons.add(chooseBtn);
		}
		return buttons;
	}
	
	private Polygon initBounds(int index, List<Entity> entities) {
		final double HEIGHT = ChooseEntityButton.HEIGHT;
		final double WIDTH = Math.min(ChooseEntityButton.WIDTH, PANEL_RECT.width / entities.size());
		double left = (PANEL_RECT.width - WIDTH * entities.size()) / 2;
		return Utils.mkCenteredRect(new Vec(left + index * WIDTH, PANEL_RECT.y + HEIGHT / 2), WIDTH / 2, HEIGHT / 2);
	}
	
	public void tick() {
		chooseEntityBar.tick(select);
	}

	public void disableControls() {
		select = false;
	}

	public void findControls() {
		select = Game.get().input.select.click();
	}
}
