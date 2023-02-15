package button;

import java.util.Comparator;
import java.util.List;

public class ButtonGroup {
	
	public List<Button> buttons;
	
	public ButtonGroup(List<Button> buttons) {
		this.buttons = buttons;
	}
	
	public void tick(boolean cursorPress) {
		buttons.sort(Comparator.comparing(b -> b.depth));

		boolean activatedOne = false;
		
		for (Button button : buttons) {
			button.press = cursorPress;
			button.calc();
			
			if (activatedOne) {
				button.isHovered = false;
			} else {
				if (button.isHovered) {
					activatedOne = true;
					if (button.press)
						button.onPress.run();
				}
			}
		}
	}

}
