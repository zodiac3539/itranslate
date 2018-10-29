package edu.nd;

import edu.nd.EnumCollection;
import java.awt.Shape;

public class ChoiShape {
    private EnumCollection.SelectionShape form;
    private Shape shape;
    //private Shape shape;
	public EnumCollection.SelectionShape getForm() {
		return form;
	}
	public void setForm(EnumCollection.SelectionShape form) {
		this.form = form;
	}
	public Shape getShape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}
    
}
