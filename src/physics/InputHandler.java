package physics;

public class InputHandler {

	private double playerHeadX = 12.5, playerHeadY = 47.5;
	boolean up, down, left, right;
	boolean shoot;
	int mouseX, mouseY;

	public InputHandler(){
		up = false;
		down = false;
		left = false;
		right = false;
		shoot = false;
		mouseX = 0;
		mouseY = 0;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isShooting() {
		return shoot;
	}

	public void setShoot(boolean shoot) {
		this.shoot = shoot;
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public double getAngle(){

		double deltax = mouseX - (1.65 * playerHeadX);
		double deltay = playerHeadY - mouseY;
		double angle = Math.atan2(deltax, deltay);
		return angle;
	}

}
