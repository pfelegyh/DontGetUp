package com.meandmyphone.chupacabraremote.ui.help;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.ui.listeners.HelpLayoutChangeListener;
import com.meandmyphone.chupacabraremote.ui.views.HelpCircleCutoutDrawable;
import com.meandmyphone.chupacabraremote.ui.views.HelpRectBorderCutoutDrawable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Help {

    private final CoordinatorLayout root;
    private final View contentRoot;
    private final List<Page> pages = new LinkedList<>();
    private final AppCompatImageButton helpButton;
    private final AppCompatTextView helperText;
    private final AppCompatImageView cutoutOverlay;
    private int yOffset = 0;
    private boolean showing;
    private int currentIndex = -1;

    private Help(View contentRoot) {

        this.contentRoot = contentRoot;

        root = (CoordinatorLayout) contentRoot.getParent();
        this.helpButton = root.findViewById(R.id.nexthelpPage);
        this.helperText = root.findViewById(R.id.helptText);
        this.cutoutOverlay = root.findViewById(R.id.cutuoutOverlay);
        helpButton.setOnClickListener(click -> show());
        cutoutOverlay.setOnClickListener(click -> show());
    }

    public static HelpBuilder newBuilder(View helpRoot) {
        return new HelpBuilder(helpRoot);
    }

    public void show() {
        showing = true;
        do {
            currentIndex++;
        } while (
                getCurrentPage() != null && getCurrentPage().onCondition != null
                        && !getCurrentPage().onCondition.test(null));

        if (currentIndex < pages.size()) {

            Page current = pages.get(currentIndex);
            if (current.view == null && current.viewProvider != null) {
                current.view = current.viewProvider.apply(null);
            }

            helperText.setText(current.text);
            helpButton.setVisibility(View.VISIBLE);
            helperText.setVisibility(View.VISIBLE);
            cutoutOverlay.setVisibility(View.VISIBLE);

            if (currentIndex == 0) {
                for (Page p : pages) {
                    if (p.onHelpStarted != null) {
                        p.onHelpStarted.accept(null);
                    }
                }

                contentRoot.setMinimumHeight(contentRoot.getHeight() + helperText.getHeight() + helpButton.getHeight());
                root.addOnLayoutChangeListener(new HelpLayoutChangeListener(this, this::createCutout));
                cutoutOverlay.setLayoutParams(new CoordinatorLayout.LayoutParams(root.getWidth(), root.getHeight() + helperText.getHeight() + helpButton.getHeight()));

            }

            if (current.onPageShown != null) {
                current.onPageShown.accept(null);
            }

            createCutout(current);
            repositionHelpTextIfNecessary(current.textPosition);

        } else {

            for (Page p : pages) {
                if (p.onHelpFinished != null) {
                    p.onHelpFinished.accept(null);
                }
            }

            helpButton.setVisibility(View.GONE);
            helperText.setVisibility(View.GONE);
            cutoutOverlay.setVisibility(View.GONE);
            currentIndex = -1;
            contentRoot.setMinimumHeight(0);
            showing = false;
        }
    }

    public void skip() {
        currentIndex = pages.size();
        show();
    }

    public AppCompatImageView getCutoutOverlay() {
        return cutoutOverlay;
    }

    public Page getCurrentPage() {
        if (currentIndex >= 0 && currentIndex < pages.size()) {
            return pages.get(currentIndex);
        } else {
            return null;
        }
    }

    private void createCutout(Page current) {
        Point positionInRoot = getPositionInRoot(current.view);

        if (current.cutoutType == CutoutType.CIRCLE) {
            cutoutOverlay.setImageDrawable(new HelpCircleCutoutDrawable(contentRoot.getContext(),
                    new RectF(0, 0, root.getWidth(), root.getHeight()),
                    positionInRoot.x + current.view.getWidth() / 2.0f,
                    positionInRoot.y + current.view.getHeight() / 2.0f + yOffset,
                    Math.min(200.0f, current.view.getWidth() / 1.7f)));
        } else if (current.cutoutType == CutoutType.RECT) {
            cutoutOverlay.setImageDrawable(new HelpRectBorderCutoutDrawable(contentRoot.getContext(),
                    new RectF(0, 0, root.getWidth(), root.getHeight()),
                    positionInRoot.x,
                    positionInRoot.y + yOffset,
                    positionInRoot.x + current.view.getWidth(),
                    positionInRoot.y + current.view.getHeight() + yOffset,
                    false
            ));
        } else if (current.cutoutType == CutoutType.INNER_RECT) {
            cutoutOverlay.setImageDrawable(new HelpRectBorderCutoutDrawable(contentRoot.getContext(),
                    new RectF(0, 0, root.getWidth(), root.getHeight()),
                    positionInRoot.x,
                    positionInRoot.y + yOffset,
                    positionInRoot.x + current.view.getWidth(),
                    positionInRoot.y + current.view.getHeight() + yOffset,
                    true
            ));
        }

    }

    private void repositionHelpTextIfNecessary(TextPosition position) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) helperText.getLayoutParams();
        if (TextPosition.TOP.equals(position)) {
            params.anchorGravity = Gravity.TOP;
            params.setAnchorId(R.id.contentRoot);
        } else if (TextPosition.CENTER.equals(position)) {
            params.anchorGravity = Gravity.CENTER;
            params.setAnchorId(R.id.contentRoot);
        } else if (TextPosition.BOTTOM.equals(position)) {
            params.anchorGravity = Gravity.TOP;
            params.setAnchorId(R.id.nexthelpPage);
        }
        helperText.invalidate();

    }

    private Point getPositionInRoot(View view) {
        View v = view;
        int x = 0;
        int y = 0;

        while (v.getId() != root.getId()) {
            x += v.getX();
            y += v.getY();
            v = (View) v.getParent();
        }
        return new Point(x, y);
    }

    public boolean isShowing() {
        return showing;
    }

    public static class HelpBuilder {

        private final Help help;

        private HelpBuilder(View root) {
            this.help = new Help(root);
        }

        public HelpBuilder addPage(Page page) {
            help.pages.add(page);
            return this;
        }

        public HelpBuilder withYOffset(int yOffset) {
            help.yOffset = yOffset;
            return this;
        }

        public Help build() {
            return help;
        }

    }

    public static class Page {
        private View view;
        private final String text;
        private final CutoutType cutoutType;
        private final TextPosition textPosition;
        private final Predicate<Void> onCondition;
        private final Consumer<Void> onHelpStarted;
        private final Consumer<Void> onPageShown;
        private final Consumer<Void> onHelpFinished;
        private final Function<Void, View> viewProvider;

        private Page(View view, Function<Void, View> viewProvider, String text, CutoutType cutoutType, TextPosition textPosition, Predicate<Void> onCondition,
                     Consumer<Void> onHelpStarted, Consumer<Void> onPageShown, Consumer<Void> onHelpFinished) {

            this.view = view;
            this.viewProvider = viewProvider;
            this.text = text;
            this.cutoutType = cutoutType;
            this.textPosition = textPosition;
            this.onCondition = onCondition;
            this.onHelpStarted = onHelpStarted;
            this.onPageShown = onPageShown;
            this.onHelpFinished = onHelpFinished;
        }

        public static PageBuilder newBuilder(Context context, View view, int stringResId, CutoutType cutoutType) {
            PageBuilder builder = new PageBuilder();
            builder.view = view;
            builder.text = context.getResources().getString(stringResId);
            builder.cutoutType = cutoutType;
            return builder;
        }

        public static PageBuilder newBuilder(Context context, Function<Void, View> viewProvider, int stringResId, CutoutType cutoutType) {
            PageBuilder builder = new PageBuilder();
            builder.viewProvider = viewProvider;
            builder.text = context.getResources().getString(stringResId);
            builder.cutoutType = cutoutType;
            return builder;
        }

        public static class PageBuilder {
            private View view;
            private Function<Void, View> viewProvider;
            private String text;
            private Help.CutoutType cutoutType;
            private Help.TextPosition textPosition = Help.TextPosition.BOTTOM;
            private Predicate<Void> onCondition = null;
            private Consumer<Void> onHelpStarted = null;
            private Consumer<Void> onPageShown = null;
            private Consumer<Void> onHelpFinished = null;

            private PageBuilder() {
            }

            public PageBuilder setTextPosition(Help.TextPosition textPosition) {
                this.textPosition = textPosition;
                return this;
            }

            public PageBuilder setOnCondition(Predicate<Void> onCondition) {
                this.onCondition = onCondition;
                return this;
            }

            public PageBuilder setOnHelpStarted(Consumer<Void> onHelpStarted) {
                this.onHelpStarted = onHelpStarted;
                return this;
            }

            public PageBuilder setOnPageShown(Consumer<Void> onPageShown) {
                this.onPageShown = onPageShown;
                return this;
            }

            public PageBuilder setOnHelpFinished(Consumer<Void> onHelpFinished) {
                this.onHelpFinished = onHelpFinished;
                return this;
            }

            public Help.Page build() {
                return new Help.Page(view, viewProvider, text, cutoutType, textPosition, onCondition, onHelpStarted, onPageShown, onHelpFinished);
            }
        }
    }

    public enum CutoutType {
        CIRCLE, RECT, INNER_RECT
    }

    public enum TextPosition {
        TOP, CENTER, BOTTOM
    }
}
