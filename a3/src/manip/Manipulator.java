package manip;

import static org.lwjgl.opengl.GL11.*;

import egl.math.*;
import gl.*;
import mesh.MeshData;

/**
 * @author srm
 *
 * An instance of this class is a single axis manipulator, which responds to user
 * input by changing one aspect of an object's transformation.
 */
public abstract class Manipulator implements Renderable {

	// Axis of manipulator, X, Y, or Z
	protected ManipulatorAxis axis;
	// The RenderObject that this manipulator controls
	protected RenderObject reference;
	// The mesh of the manipulator
	protected RenderMesh mesh;
	// Whether the mouse selects this manipulator
	protected boolean isSelected;

	public Manipulator () {
		MeshData data = new MeshData();
		data.loadOBJ(meshPath());
		this.mesh = new RenderMesh(data);
	}

	public Manipulator (RenderObject referenced) {
		this();
		this.reference = referenced;
	}

	@Override
	public RenderMesh getMesh () {
		return mesh;
	}

	@Override
	public Matrix4 getWorldTransform () {
		return getReferencedTransform().clone().mulBefore(getLocalTransform());
	}

	@Override
	public Matrix3 getWorldTransformIT () {
		return getWorldTransform().getAxes().invert().transpose();
	}

	/**
	 * Get the world transform of the referenced object
	 * @return the world transform of reference
	 */
	protected abstract Matrix4 getReferencedTransform();

	/**
	 * Get the object transform of the manipulator so they stay in the correct axis
	 * @return local object transform of this manipulator
	 */
	private Matrix4 getLocalTransform() {
		Matrix4 mManip = new Matrix4();
		switch (axis) {
		case X:
			Matrix4.createRotationY((float)(Math.PI / 2.0), mManip);
			break;
		case Y:
			Matrix4.createRotationX((float)(-Math.PI / 2.0), mManip);
			break;
		case Z:
			mManip.setIdentity();
			break;
		}
		return mManip;
	}

	/**
	 * Modify the appropriate transformation matrix of the referenced object based on mouse input
	 * and the type of manipulator being used.
	 * @param lastMousePos The previous mouse position, normalized to the range [-1,1] for both the X and Y coordinate.
	 * @param curMousePos The new mouse position, normalized to the range [-1,1] for both the X and Y coordinate.
	 * @param viewProjection (projection matrix * view matrix) from the current camera.
	 */
	public abstract void applyTransformation(Vector2 lastMousePos, Vector2 curMousePos, Matrix4 viewProjection);

	/**
	 * Set the RenderObject being referenced
	 */
	public void setReferencedObject (RenderObject reference) {
		this.reference = reference;
	}

	/**
	 * Where to find the mesh of this manipulator
	 */
	protected abstract String meshPath ();

	@Override
	public Colorf getColor () {
		Colorf color = new Colorf();
		switch (axis) {
		case X:
			color.set(Color.Red);
			if (isSelected) {
				color.y = 0.8f;
				color.z = 0.8f;
			}
			break;
		case Y:
			color.set(Color.Lime);
			if (isSelected) {
				color.x = 0.8f;
				color.z = 0.8f;
			}
			break;
		case Z:
			color.set(Color.Blue);
			if (isSelected) {
				color.x = 0.8f;
				color.y = 0.8f;
			}
			break;
		}
		return color;
	}

	/**
	 * For shader usage. 0 if a constant color should be used
	 */
	@Override
	public float getMode() {
		return 0f;
	}

	@Override
	public void render () {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ZERO);
		mesh.render();
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		mesh.render();
	}

	/**
	 * Set whether this manipulator is selected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
