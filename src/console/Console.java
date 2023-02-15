package console;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import base.Game;
import math.Vec;
import miscObjects.Noted;
import ui.Controllable;

public class Console implements Controllable {
	
	public JTextArea textArea;
	public JScrollPane scrollPane;
	public Vec selectorPos;
	public DisplayedText displayedText;
	public List<String> userInputs;
	
	private enum Visibility {
		HIDDEN,
		VISIBLE,
		MARK_HIDDEN
	}
	private Visibility visibility;
	private boolean submitted;
	private final ArrayList<Runnable> doQueue = new ArrayList<>();
	private String lastInput;
	private ConsolePanel parentPanel;
	private static final String TOGGLE_CONSOLE_HIDE = "TOGGLE_CONSOLE_HIDE";
	private static final String CONSOLE_SUBMIT = "CONSOLE_SUBMIT";
	private Object currCaller;
	
	public void init(Vec selectorPos, ConsolePanel parentPanel) {
		this.parentPanel = parentPanel;
		visibility = Visibility.HIDDEN;
		submitted = false;
		userInputs = new ArrayList<>();
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setSize((int) (Game.get().getWidth() * 0.7), (int) (Game.get().getHeight() * 0.1));
		scrollPane.setLocation((Game.get().getWidth() - scrollPane.getWidth()) / 2, Game.get().getHeight() - scrollPane.getHeight() - 20);
		
		//	Ctrl h is part of this
//		System.out.println(Arrays.toString(textArea.getInputMap().allKeys()));
		
		initTextAreaActions();
		
		Action onToggleConsoleHide = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				visibility = Visibility.MARK_HIDDEN;
				textArea.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
				textArea.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
			}
		};
		
		textArea.getActionMap().put(TOGGLE_CONSOLE_HIDE, onToggleConsoleHide);
		
		Action onConsoleSubmit = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				submitted = true;
			}
		};
		
		textArea.getActionMap().put(CONSOLE_SUBMIT, onConsoleSubmit);
		
		displayedText = new DisplayedText(selectorPos, scrollPane);
		displayedText.init();
	}
	
	public boolean callingAndFits(Object caller, Function<String, Boolean> fitChecker) {
		Noted<InputValidity, String> okInput = getLastOkInput();
		
		if (okInput.enumVal == InputValidity.CALLED
			&& !isCurrCaller(currCaller))
			return false;
		
		return fitChecker.apply(okInput.val);
	}
	
	private void initTextAreaActions() {
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), TOGGLE_CONSOLE_HIDE);
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), CONSOLE_SUBMIT);
	}
	
	private void hide() {
		visibility = Visibility.HIDDEN;
		
		parentPanel.bounds.vertices = new ArrayList<>(List.of(new Vec()));
		Game.get().pane.remove(scrollPane);
	}
	
	private void show() {
		visibility = Visibility.VISIBLE;

		initTextAreaActions();
		parentPanel.bounds = parentPanel.defBounds;
		Game.get().pane.add(scrollPane);
		textArea.grabFocus();
	}
	
	public void disableControls() {
//		hidden = true;
//		submit = false;
	}
	
	public void findControls() {
		
	}
	
	public void tick() {
		doQueue.forEach(Runnable::run);
		doQueue.clear();
		if (submitted)
			doQueue.add(() -> submitted = false);
		
		displayedText.tick();
		
		boolean toggle = Game.get().input.toggleConsole.click();
		
		switch (visibility) {
			case HIDDEN: {
				if (toggle)
					show();
				break;
			}
			case VISIBLE: {
				boolean submit = Game.get().input.consoleSubmit.click();
				if (submit || submitted) {
					lastInput = getText();
					doQueue.add(() -> lastInput = null);
					
					userInputs.add(lastInput);
					displayedText.lines.add(lastInput);
					setText("");
				}
				if (toggle)
					hide();
				break;
			}
			case MARK_HIDDEN: {
				hide();
				break;
			}
		}
	}
	
	public Console endCaller() {
		doQueue.add(() -> currCaller = null);
		return this;
	}
	
	public boolean isCurrCaller(Object currCaller) {
		return this.currCaller == currCaller;
	}
	
	public Console setCurrCaller(Object currCaller) {
		this.currCaller = currCaller;
		return this;
	}
	
	public Object getCurrCaller() {
		return currCaller;
	}
	
	public Console print(String str, Object currCaller) {
		setCurrCaller(currCaller);
		print(str);
		return this;
	}
	
	public Console print(String str) {
		displayedText.lines.add(str);
		return this;
	}
	
	public Console forceHide() {
		if (visibility != Visibility.HIDDEN)
			hide();
		return this;
	}
	
	public Console forceShow() {
		if (visibility != Visibility.VISIBLE)
			show();
		return this;
	}
	
	public void render() {
		if (visibility == Visibility.VISIBLE)
			render(Game.get().getGraphics(displayedText.depth));
	}
	
	private void render(Graphics g) {
		displayedText.render.accept(g);
	}
	
	public void setText(String info) {
		textArea.setText(info);
	}
	
	public enum InputValidity {
		NULL,
		CALLED,
		OK
	}
	public Noted<InputValidity, String> getLastOkInput() {
		InputValidity validity = InputValidity.OK;
		if (currCaller != null)
			validity = InputValidity.CALLED;
		if (lastInput == null)
			validity = InputValidity.NULL;
		return new Noted<>(validity, lastInput);
	}
	
	public String getLastInput() {
		return lastInput;
	}
	
	public boolean justSubmitted() {
		return submitted;
	}
	
	public String getText() {
		return textArea.getText();
	}
	
}
