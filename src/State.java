package state;

import java.util.ArrayList;
import java.util.List;

import math.Vec;
import ui.Panel;
import ui.PanelManager;

public abstract class State {
	
	protected final ArrayList<Runnable> renderers = new ArrayList<>();
	protected PanelManager panelManager = new PanelManager();
	protected Vec selectorPos;
	
	public final void init() {
		_init();
	}
	
	public final void initPanelManager(List<Panel> panels, Vec selectorPos) {
		panelManager.addAll(panels);
		this.selectorPos = selectorPos;
	}
	
	public abstract void _init();

	public final void tick() {
		panelManager.managePanels(selectorPos);
		_tick();
	}
	
	public abstract void _tick();
	
	public final void render() {
		_render();
		renderers.forEach(Runnable::run);
		renderers.clear();
	}

	public abstract void _render();

}
