package rendering;

import javafx.scene.image.ImageView;

/**
 * A prop acts just like a <code>Wall</code>, except each prop is on its own and acts more like a decoration to the map.
 * @see Wall
 */
class Prop
{
	String material;
	int x, y;
	transient ImageView image;
}
