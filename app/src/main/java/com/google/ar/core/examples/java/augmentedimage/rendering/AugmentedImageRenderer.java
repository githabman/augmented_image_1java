/*
 * Copyright 2018 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.examples.java.augmentedimage.rendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.augmentedimage.rendering.ObjectRenderer.BlendMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/** Renders an augmented image. */
public class AugmentedImageRenderer {
  private static final String TAG = "AugmentedImageRenderer";

  private static final float TINT_INTENSITY = 0.1f;
  private static final float TINT_ALPHA = 1.0f;
  private static final int[] TINT_COLORS_HEX = {
    0x000000, 0xF44336, 0xE91E63, 0x9C27B0, 0x673AB7, 0x3F51B5, 0x2196F3, 0x03A9F4, 0x00BCD4,
    0x009688, 0x4CAF50, 0x8BC34A, 0xCDDC39, 0xFFEB3B, 0xFFC107, 0xFF9800,
  };

  private boolean showPaper = false;

  private final ObjectRenderer corkboardObj = new ObjectRenderer();

  private final ObjectRenderer paper = new ObjectRenderer();

  public AugmentedImageRenderer() {}


  public void createOnGlThread(Context context) throws IOException {

   // InputStream objInputStream = context.getAssets().open("file");
    corkboardObj.createOnGlThread(
        context, "models/Test3.obj", "models/wood.png");
       // Log.v("",""+);
    corkboardObj.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
    corkboardObj.setBlendMode(BlendMode.SourceAlpha);



  }


  public void drawPaper(Context context, InputStream picturestream) {

    try {
      paper.createOnGlThread(context, "models/PaperTest1.obj", picturestream.);
      showPaper = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void draw(
      float[] viewMatrix,
      float[] projectionMatrix,
      AugmentedImage augmentedImage,
      Anchor centerAnchor,
      float[] colorCorrectionRgba) {
    float[] tintColor =
        convertHexToColor(TINT_COLORS_HEX[augmentedImage.getIndex() % TINT_COLORS_HEX.length]);

    Pose[] localBoundaryPoses = {
      Pose.makeTranslation(
          -0.5f * augmentedImage.getExtentX(),
          0.0f,
          -0.5f * augmentedImage.getExtentZ()), // upper left
      Pose.makeTranslation(
          0.5f * augmentedImage.getExtentX(),
          0.02f,
          -0.5f * augmentedImage.getExtentZ()), // upper right
      Pose.makeTranslation(
          0.5f * augmentedImage.getExtentX(),
          0.0f,
          0.5f * augmentedImage.getExtentZ()), // lower right
      Pose.makeTranslation(
          -0.5f * augmentedImage.getExtentX(),
          0.0f,
          0.5f * augmentedImage.getExtentZ()) // lower left
    };

    Pose anchorPose = centerAnchor.getPose();
    Pose[] worldBoundaryPoses = new Pose[4];
    for (int i = 0; i < 4; ++i) {
      worldBoundaryPoses[i] = anchorPose.compose(localBoundaryPoses[i]);
    }

    float scaleFactor = 1.0f;
    float[] modelMatrix = new float[16];

    worldBoundaryPoses[0].toMatrix(modelMatrix, 0);
    corkboardObj.updateModelMatrix(modelMatrix, scaleFactor);
    corkboardObj.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);

    if (showPaper) {
      worldBoundaryPoses[1].toMatrix(modelMatrix, 0);
      paper.updateModelMatrix(modelMatrix, 0.05f);
      paper.draw(viewMatrix, projectionMatrix, colorCorrectionRgba, tintColor);
    }

  }

  private static float[] convertHexToColor(int colorHex) {
    // colorHex is in 0xRRGGBB format
    float red = ((colorHex & 0xFF0000) >> 16) / 255.0f * TINT_INTENSITY;
    float green = ((colorHex & 0x00FF00) >> 8) / 255.0f * TINT_INTENSITY;
    float blue = (colorHex & 0x0000FF) / 255.0f * TINT_INTENSITY;
    return new float[] {red, green, blue, TINT_ALPHA};
  }
  public void removeFromGlThread(Context context) throws IOException {

    // remove the poster from the GlThread.


  }



}
