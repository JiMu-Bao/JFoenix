package com.cctintl.c3dfx.skins;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import com.cctintl.c3dfx.controls.C3DCheckBox;
import com.cctintl.c3dfx.controls.C3DRippler;
import com.cctintl.c3dfx.controls.C3DRippler.RipplerMask;
import com.sun.javafx.scene.control.skin.CheckBoxSkin;

public class C3DCheckBoxSkin extends CheckBoxSkin {

	private final StackPane box = new StackPane();
	private double lineThick = 2;
	private double padding = 10;
	private double boxWidth;
	private double maxHeight;
	private double boxHeight;
	private final C3DRippler rippler;

	private Line rightLine;
	private Line leftLine;

	private final AnchorPane container = new AnchorPane();
	private double labelOffset = 0;

	private Timeline transition;

	private boolean invalid = true;

	public C3DCheckBoxSkin(C3DCheckBox control) {
		super(control);

		box.setMinSize(20, 20);
		box.setPrefSize(20, 20);
		box.setMaxSize(20, 20);
		box.setBorder(new Border(new BorderStroke(control.getUnCheckedColor(),BorderStrokeStyle.SOLID,new CornerRadii(0), new BorderWidths(lineThick))));
		//		
		StackPane boxContainer = new StackPane();
		boxContainer.getChildren().add(box);
		boxContainer.setPadding(new Insets(padding));
		rippler = new C3DRippler(boxContainer,RipplerMask.CIRCLE);
		rippler.setRipplerFill(getSkinnable().isSelected()?control.getUnCheckedColor():control.getCheckedColor());

		rightLine = new Line();
		leftLine = new Line(); 
		rightLine.setStroke(control.getCheckedColor());
		rightLine.setStrokeWidth(lineThick);
		leftLine.setStroke(control.getCheckedColor());
		leftLine.setStrokeWidth(lineThick);
		rightLine.setVisible(false);
		leftLine.setVisible(false);

		container.getChildren().add(rightLine);
		container.getChildren().add(leftLine);
		container.getChildren().add(rippler);
		AnchorPane.setRightAnchor(rippler, labelOffset);

		// add listeners
		getSkinnable().selectedProperty().addListener((o,oldVal,newVal) ->{
			rippler.setRipplerFill(newVal?control.getUnCheckedColor():control.getCheckedColor());
			transition.setRate(newVal?1:-1);
			transition.play();
		});

		updateChildren();

	}

	@Override protected void updateChildren() {
		super.updateChildren();
		if (container != null) {
			getChildren().remove(1);			
			getChildren().add(container);
		}
	}

	@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(box.minWidth(-1))+labelOffset+2*padding;
	}

	@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSize(box.prefWidth(-1))+labelOffset+2*padding;
	}

	@Override 
	protected void layoutChildren(final double x, final double y, final double w, final double h) {

		final CheckBox checkBox = getSkinnable(); 
		boxWidth = snapSize(container.prefWidth(-1));
		boxHeight = snapSize(container.prefHeight(-1));
		final double computeWidth = Math.min(checkBox.prefWidth(-1),checkBox.minWidth(-1))+labelOffset+2*padding;
		final double labelWidth = Math.min( computeWidth - boxWidth, w - snapSize(boxWidth))+labelOffset+2*padding;
		final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
		maxHeight = Math.max(boxHeight, labelHeight);
		final double xOffset = computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
		final double yOffset = computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + x;

		if(invalid){
			rightLine.setStartX((boxWidth+padding-labelOffset)/2 - boxWidth/5.5 );
			rightLine.setStartY(maxHeight-padding-lineThick);
			rightLine.setEndX((boxWidth+padding-labelOffset)/2 - boxWidth/5.5 );
			rightLine.setEndY( maxHeight-padding-lineThick);
			leftLine.setStartX((boxWidth+padding-labelOffset)/2 - boxWidth/5.5 );
			leftLine.setStartY(maxHeight-padding-lineThick);
			leftLine.setEndX((boxWidth+padding-labelOffset)/2 - boxWidth/5.5 );
			leftLine.setEndY(maxHeight-padding-lineThick);
			transition = getCheckBoxTransition();
			invalid = false;
		}

		layoutLabelInArea(xOffset + boxWidth, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
		container.resize(boxWidth, boxHeight);
		positionInArea(container, xOffset, yOffset, boxWidth, maxHeight, 0, checkBox.getAlignment().getHpos(), checkBox.getAlignment().getVpos());

	}


	static double computeXOffset(double width, double contentWidth, HPos hpos) {
		switch(hpos) {
		case LEFT:
			return 0;
		case CENTER:
			return (width - contentWidth) / 2;
		case RIGHT:
			return width - contentWidth;
		}
		return 0;
	}

	static double  computeYOffset(double height, double contentHeight, VPos vpos) {

		switch(vpos) {
		case TOP:
			return 0;
		case CENTER:
			return (height - contentHeight) / 2;
		case BOTTOM:
			return height - contentHeight;
		default:
			return 0;
		}
	}


	private Timeline getCheckBoxTransition(){
		return new Timeline(
				new KeyFrame(
						Duration.ZERO,       
						new KeyValue(rightLine.visibleProperty(), false,Interpolator.EASE_BOTH),
						new KeyValue(leftLine.visibleProperty(), false,Interpolator.EASE_BOTH),
						new KeyValue(box.rotateProperty(), 0 ,Interpolator.EASE_BOTH),
						new KeyValue(box.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
						new KeyValue(box.scaleYProperty(), 1 ,Interpolator.EASE_BOTH),
						new KeyValue(box.translateYProperty(), 0 ,Interpolator.EASE_BOTH),
						new KeyValue(box.translateXProperty(), 0 ,Interpolator.EASE_BOTH),
						new KeyValue(box.opacityProperty(), 1 ,Interpolator.EASE_BOTH)
						),
						new KeyFrame(Duration.millis(160),
								new KeyValue(rightLine.visibleProperty(), true,Interpolator.EASE_BOTH),
								new KeyValue(leftLine.visibleProperty(), true,Interpolator.EASE_BOTH),
								new KeyValue(rightLine.endXProperty(), (boxWidth+padding-labelOffset)/2 - boxWidth/5.5 ,Interpolator.EASE_BOTH),
								new KeyValue(rightLine.endYProperty(), maxHeight-padding-2*lineThick ,Interpolator.EASE_BOTH),
								new KeyValue(leftLine.endXProperty(), (boxWidth+padding-labelOffset)/2 - boxWidth/5.5 ,Interpolator.EASE_BOTH),
								new KeyValue(leftLine.endYProperty(), maxHeight-padding-2*lineThick ,Interpolator.EASE_BOTH)
								),
								new KeyFrame(Duration.millis(200),
										new KeyValue(box.rotateProperty(), 44 ,Interpolator.EASE_BOTH),
										new KeyValue(box.scaleXProperty(), 0.3 ,Interpolator.EASE_BOTH),
										new KeyValue(box.scaleYProperty(), 0.4 ,Interpolator.EASE_BOTH),
										new KeyValue(box.translateYProperty(), boxHeight/12  ,Interpolator.EASE_BOTH),
										new KeyValue(box.translateXProperty(), - boxWidth/12 ,Interpolator.EASE_BOTH)										
										),											
										new KeyFrame(Duration.millis(280),
												new KeyValue(box.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
												),
												new KeyFrame(
														Duration.millis(400),
														new KeyValue(rightLine.endXProperty(), boxWidth-padding-labelOffset + lineThick/2 ,Interpolator.EASE_BOTH),
														new KeyValue(rightLine.endYProperty(), (maxHeight-padding)/2.4 ,Interpolator.EASE_BOTH),
														new KeyValue(leftLine.endXProperty(), padding + lineThick/4 ,Interpolator.EASE_BOTH),
														new KeyValue(leftLine.endYProperty(), (maxHeight-padding)/1.4 ,Interpolator.EASE_BOTH)
														)

				);
	}

	//	private class CheckBoxTransition extends CachedTimelineTransition {
	//
	//		public CheckBoxTransition() {
	//			super(box, new Timeline(
	//					new KeyFrame(
	//							Duration.ZERO,       
	//							new KeyValue(rightLine.visibleProperty(), false,Interpolator.EASE_BOTH),
	//							new KeyValue(leftLine.visibleProperty(), false,Interpolator.EASE_BOTH),
	//							new KeyValue(box.rotateProperty(), 0 ,Interpolator.EASE_BOTH),
	//							new KeyValue(box.scaleXProperty(), 1 ,Interpolator.EASE_BOTH),
	//							new KeyValue(box.scaleYProperty(), 1 ,Interpolator.EASE_BOTH),
	//							new KeyValue(box.translateYProperty(), 0 ,Interpolator.EASE_BOTH),
	//							new KeyValue(box.translateXProperty(), 0 ,Interpolator.EASE_BOTH),
	//							new KeyValue(box.opacityProperty(), 1 ,Interpolator.EASE_BOTH)
	//							),
	//							new KeyFrame(Duration.millis(400),
	//									new KeyValue(rightLine.visibleProperty(), true,Interpolator.EASE_BOTH),
	//									new KeyValue(leftLine.visibleProperty(), true,Interpolator.EASE_BOTH),
	//									new KeyValue(rightLine.endXProperty(), (boxWidth+padding-labelOffset)/2 - boxWidth/5.5 ,Interpolator.EASE_BOTH),
	//									new KeyValue(rightLine.endYProperty(), maxHeight-padding-2*lineThick ,Interpolator.EASE_BOTH),
	//									new KeyValue(leftLine.endXProperty(), (boxWidth+padding-labelOffset)/2 - boxWidth/5.5 ,Interpolator.EASE_BOTH),
	//									new KeyValue(leftLine.endYProperty(), maxHeight-padding-2*lineThick ,Interpolator.EASE_BOTH)
	//									),
	//									new KeyFrame(Duration.millis(500),
	//											new KeyValue(box.rotateProperty(), 44 ,Interpolator.EASE_BOTH),
	//											new KeyValue(box.scaleXProperty(), 0.3 ,Interpolator.EASE_BOTH),
	//											new KeyValue(box.scaleYProperty(), 0.4 ,Interpolator.EASE_BOTH),
	//											new KeyValue(box.translateYProperty(), boxHeight/12  ,Interpolator.EASE_BOTH),
	//											new KeyValue(box.translateXProperty(), - boxWidth/12 ,Interpolator.EASE_BOTH)										
	//											),											
	//											new KeyFrame(Duration.millis(700),
	//													new KeyValue(box.opacityProperty(), 0 ,Interpolator.EASE_BOTH)
	//													),
	//													new KeyFrame(
	//															Duration.millis(800),
	//															new KeyValue(rightLine.endXProperty(), boxWidth-padding-labelOffset + lineThick/2 ,Interpolator.EASE_BOTH),
	//															new KeyValue(rightLine.endYProperty(), (maxHeight-padding)/2.4 ,Interpolator.EASE_BOTH),
	//															new KeyValue(leftLine.endXProperty(), padding + lineThick/4 ,Interpolator.EASE_BOTH),
	//															new KeyValue(leftLine.endYProperty(), (maxHeight-padding)/1.4 ,Interpolator.EASE_BOTH)
	//															)
	//
	//					)
	//					);
	//			// reduce the number to increase the shifting , increase number to reduce shifting
	//			setCycleDuration(Duration.seconds(0.4));
	//			setDelay(Duration.seconds(0));
	//		}
	//
	//	}


}