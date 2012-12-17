package com.fizzbuzz.android.ui;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

// Note: although significantly modified, the general techniques used in this class are borrowed from http://code.google.com/p/android-flip3d/
public class ViewRevolver
{
    private final FrameLayout mFrame;;

    private final View mBackView;
    private final View mFrontView;
    private View mCurrentView;
    private final Animation mAnimation = new RevolveAnimation();

    public enum Side {
        FRONT,
        BACK
    }

    // private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    public ViewRevolver(FrameLayout frame, View frontView, View backView) {
        mFrame = frame;
        mFrontView = frontView;
        mBackView = backView;
    }

    public Side getCurrentSide() {
        return mCurrentView == mFrontView ? Side.FRONT : Side.BACK;
    }

    public View getCurrentView() {
        return mCurrentView;
    }

    public View getOtherSideView() {
        return mCurrentView == mFrontView ? mBackView : mFrontView;
    }

    public void revolve() {
        mFrame.startAnimation(mAnimation);

    }

    public void showView(Side side) {
        showView(side == Side.FRONT ? mFrontView : mBackView);
    }

    public void showView(View view) {
        mCurrentView = view;
        getOtherSideView().setVisibility(View.GONE);
        mCurrentView.setVisibility(View.VISIBLE);
        mCurrentView.bringToFront();
        mCurrentView.requestFocus();
    }

    private class RevolveAnimation
            extends Animation {
        private float mFromDegrees;
        private float mToDegrees;
        private float mCenterX;
        private float mCenterY;
        private Camera mCamera;
        private final float mDepthZ = 1500.0f;
        boolean mReverse;

        public RevolveAnimation() {
            super();
            setDuration(800);
            setInterpolator(new AccelerateDecelerateInterpolator());
        }

        @Override
        public void initialize(final int width, final int height,
                final int parentWidth, final int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);

            /*
             * mLogger.info(
             * "RevolveAnimation.initialize: {}",
             * "width=" + Integer.toString(width) + " height="
             * + Integer.toString(height) + " parentWidth="
             * + Integer.toString(parentWidth) + " parentHeight="
             * + Integer.toString(parentHeight));
             */

            mFromDegrees = 0.0f;
            mToDegrees = 90.0f;
            mReverse = false;
            mCenterX = parentWidth / 2.0f;
            mCenterY = parentHeight / 2.0f;
            mCamera = new Camera();
        }

        @Override
        protected void applyTransformation(final float interpolatedTime,
                final Transformation t) {
            // mLogger.trace("RevolveAnimation.applyTransformation: interpolatedTime= {}",
            // Float.toString(interpolatedTime));

            if (interpolatedTime == 0.0f || interpolatedTime == 1.0f)
                return; // don't waste time on these cases

            // we need to translate a single animation into two phases. In phase 1, we rotate the frame 0 to 90 degrees
            // while pushing it deeper into the Z axis. In phase 2, we rotate from -90 to 0 degrees while bringing the
            // frame back to the front of the screen. For simplicity in the rest of this method, we therefore translate
            // the incoming interpolated time, which goes from 0.0 to 1.0, into two sequences that EACH go from 0.0 to
            // 1.0.
            float translatedInterpolatedTime;
            if (interpolatedTime < 0.5f) {
                translatedInterpolatedTime = interpolatedTime * 2.0f;
            }
            else {
                translatedInterpolatedTime = (interpolatedTime - 0.5f) * 2.0f;
            }

            // at the halfway point, switch into "reverse" and bring the other side view to the front
            if (mReverse == false && interpolatedTime >= .5f) {
                mReverse = true;
                mFromDegrees = -90.0f;
                mToDegrees = 0.0f;
                showView(getOtherSideView());
            }

            mCamera.save();

            // push or pull the frame in the Z axis depending on whether we're in the 1st or 2nd phase
            if (!mReverse) {
                mCamera.translate(0.0f, 0.0f, mDepthZ * translatedInterpolatedTime);
            }
            else {
                mCamera.translate(0.0f, 0.0f, mDepthZ * (1.0f - translatedInterpolatedTime));
            }

            // rotate around the Y axis
            final float degrees = mFromDegrees + (mToDegrees - mFromDegrees) * translatedInterpolatedTime;

            mCamera.rotateY(degrees);

            // shift everything to the center before the other translations, then back afterward, so things happen
            // relative to the center of the frame rather than the upper left
            final Matrix matrix = t.getMatrix();
            mCamera.getMatrix(matrix);
            matrix.preTranslate(-mCenterX, -mCenterY);
            matrix.postTranslate(mCenterX, mCenterY);
            mCamera.restore();
        }
    }
}
