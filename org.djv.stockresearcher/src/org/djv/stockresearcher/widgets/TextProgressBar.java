package org.djv.stockresearcher.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

	public final class TextProgressBar extends ProgressBar {

        // Default text color
        private Color textColor;
        private String text;

        public TextProgressBar(final Composite parent, final int style) {
                super(parent, style);

                this.textColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);

                addPaintListener(new PaintListener() {
                    @Override
                    public void paintControl(final PaintEvent e) {
                        final Point widgetSize = getSize();
                        final Point textSize = e.gc.stringExtent(text);
                        e.gc.setForeground(TextProgressBar.this.textColor);
                        e.gc.drawString(text, ((widgetSize.x - textSize.x) / 2), ((widgetSize.y - textSize.y) / 2), true);
                    }
                });
        }

        /**
         * Set text color
         * 
         * @param textColor int
         */
        public void setTextColor(final Color textColor) {
                checkWidget();
                this.textColor = textColor;
        }

        /**
         * Return text color
         * 
         * @return int
         */
        public Color getTextColor() {
                checkWidget();
                return this.textColor;
        }

        public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
        protected void checkSubclass() {
                // Disable the check that prevents subclassing of SWT components.
        }

}
