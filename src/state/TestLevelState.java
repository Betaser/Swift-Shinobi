package state;

import levels.TestLevel;

public class TestLevelState extends State {

	TestLevel level;
	
	public void _init() {
		level = new TestLevel();
		level.init();
	}

	public void _tick() {
		level.tick();
	}

	@Override
	public void _render() {
		level.render(renderers);
	}
	
}
