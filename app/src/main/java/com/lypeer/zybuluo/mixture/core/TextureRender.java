package com.lypeer.zybuluo.mixture.core;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.lypeer.zybuluo.mixture.util.GLESUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.lypeer.zybuluo.mixture.util.GLESUtil.checkGlError;


/**
 * Code for rendering a texture onto a surface using OpenGL ES 2.0.
 */
public class TextureRender {
    private static final String TAG = "TextureRender";

    protected static final int FLOAT_SIZE_BYTES = 4;
    protected static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    protected static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    protected static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;


    /*protected final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 1.f,
            1.0f, -1.0f, 0, 1.f, 1.f,
            -1.0f, 1.0f, 0, 0.f, 0.f,
            1.0f, 1.0f, 0, 1.f, 0.f,

    };*/


    protected final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f, 1.0f, 0, 0.f, 1.f,
            1.0f, 1.0f, 0, 1.f, 1.f,
    };

    /*
        public static final float mFilterTexturesCoordinateData[] = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
    */
    public static final float mFilterTexturesCoordinateData[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    protected FloatBuffer mTriangleVertices;

    protected FloatBuffer mFilterTextureCoordinate;

    private static final String ALPHA_BLEND_VERTEX_SHADER = "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate1;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            " \n" +
            "varying vec2 textureCoordinate1;\n" +
            "varying vec2 textureCoordinate2;\n" +

            "const int GAUSSIAN_SAMPLES = 9;\n" +
            "uniform vec2 singleStepOffset;\n" +
            "varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +

            " \n" +
            "void main()\n" +
            "{\n" +
            "  gl_Position = uMVPMatrix * position;\n" +
            "  textureCoordinate1 = (uSTMatrix * inputTextureCoordinate1).xy;\n" +
            "  textureCoordinate2 = inputTextureCoordinate2.xy;\n" +

            "	int multiplier = 0;\n" +
            "	vec2 blurStep;\n" +

            "	for (int i = 0; i < GAUSSIAN_SAMPLES; i++)\n" +
            "	{\n" +
            "		multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));\n" +

            "		blurStep = float(multiplier) * singleStepOffset;\n" +
            "		blurCoordinates[i] = inputTextureCoordinate2.xy + blurStep;\n" +
            "	}\n" +

            "}";

    public static final String ALPHA_BLEND_FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "varying highp vec2 textureCoordinate1;\n" +
            "varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform samplerExternalOES inputImageTexture1;\n" +
            " uniform sampler2D inputImageTexture2;\n" +

            " const lowp int GAUSSIAN_SAMPLES = 9;\n" +
            " varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +
            " uniform mediump float distanceNormalizationFactor;\n" +

            "\n" +
            " \n" +
            " void main()\n" +
            " {\n" +

            "     lowp vec4 centralColor;\n" +
            "     lowp float gaussianWeightTotal;\n" +
            "     lowp vec4 sum;\n" +
            "     lowp vec4 sampleColor;\n" +
            "     lowp float distanceFromCentralColor;\n" +
            "     lowp float gaussianWeight;\n" +
            "     \n" +
            "     centralColor = texture2D(inputImageTexture2, blurCoordinates[4]);\n" +
            "     gaussianWeightTotal = 0.18;\n" +
            "     sum = centralColor * 0.18;\n" +
            "     \n" +
            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[0]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[1]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[2]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[3]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[5]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[6]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[7]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +

            "     sampleColor = texture2D(inputImageTexture2, blurCoordinates[8]);\n" +
            "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
            "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
            "     gaussianWeightTotal += gaussianWeight;\n" +
            "     sum += sampleColor * gaussianWeight;\n" +
            "     lowp vec4 beauty = sum / gaussianWeightTotal;\n" +
            "     beauty = sum / gaussianWeightTotal;\n" +
            "     beauty = vec4((beauty.rgb + vec3(0.2)), beauty.w);\n" +
            "   //lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "   lowp vec4 textureColor1 = texture2D(inputImageTexture1, textureCoordinate1);\n" +
            "   \n" +
            "   gl_FragColor = mix(textureColor1, beauty, beauty.a);\n" +
            " }";

    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    private int mProgram;
    private int mTextureID = -12345;
    public int mFilterTexture = GLESUtil.NO_TEXTURE;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int mPositionHandle;

    private int mTextureCoordinateHandle;
    private int mTextureCoordinateHandle2;

    public int mFilterInputTextureUniform2;

    private String mVertexShader;
    private String mFragmentShader;

    private int mDisFactorLocation;
    private int mSingleStepOffsetLocation;

    private int mWidth;
    private int mHeight;

    public TextureRender() {
        this(ALPHA_BLEND_VERTEX_SHADER, ALPHA_BLEND_FRAGMENT_SHADER);
    }

    public TextureRender(String vertexShader, String fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        mTriangleVertices = ByteBuffer.allocateDirect(
                mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);

        mFilterTextureCoordinate = ByteBuffer.allocateDirect(mFilterTexturesCoordinateData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFilterTextureCoordinate.clear();
        mFilterTextureCoordinate.put(mFilterTexturesCoordinateData).position(0);

        Matrix.setIdentityM(mSTMatrix, 0);
    }

    public int getTextureId() {
        return mTextureID;
    }

    public int getProgram() {
        return mProgram;
    }

    public void drawNone(SurfaceTexture st) {
        st.getTransformMatrix(mSTMatrix);
    }

    public void drawFrame(SurfaceTexture st, Bitmap filterBitmap) {
        checkGlError("onDrawFrame start");
        st.getTransformMatrix(mSTMatrix);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        checkGlError("glEnableVertexAttribArray mPositionHandle");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer mTextureCoordinateHandle");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        checkGlError("glEnableVertexAttribArray mTextureCoordinateHandle");


        mFilterTextureCoordinate.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle2, 2, GLES20.GL_FLOAT, false, 0,
                mFilterTextureCoordinate);
        checkGlError("glEnableVertexAttribArray mTextureCoordinateHandle2");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle2);
        checkGlError("glEnableVertexAttribArray mTextureCoordinateHandle2");

        Matrix.setIdentityM(mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

        // set bitmap 2
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        mFilterTexture = GLESUtil.loadTexture(filterBitmap, mFilterTexture, false);
        GLES20.glUniform1i(mFilterInputTextureUniform2, 2);

        GLES20.glUniform2fv(mSingleStepOffsetLocation, 1, FloatBuffer.wrap(new float[] {1.0f / mWidth, 1.0f / mHeight}));
        GLES20.glUniform1f(mDisFactorLocation, (float)6);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
        GLES20.glFinish();
        //Log.v("TextureRender", "drawFrame");
    }

    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    public void surfaceCreated() {
        mProgram = GLESUtil.loadProgram(mVertexShader, mFragmentShader);
        GLES20.glUseProgram(mProgram);
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
        // position location
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
        checkGlError("glGetAttribLocation position");
        if (mPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for position");
        }

        // coordinate location
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate1");
        checkGlError("glGetAttribLocation inputTextureCoordinate");
        if (mTextureCoordinateHandle == -1) {
            throw new RuntimeException("Could not get attrib location for inputTextureCoordinate");
        }

        // coordinate2 location
        mTextureCoordinateHandle2 = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate2");
        checkGlError("glGetAttribLocation inputTextureCoordinate2");
        if (mTextureCoordinateHandle2 == -1) {
            throw new RuntimeException("Could not get attrib location for inputTextureCoordinate2");
        }

        // inputImageTexture2 location
        mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        if (mFilterInputTextureUniform2 == -1) {
            throw new RuntimeException("Could not get attrib uniform for inputImageTexture2");
        }

        mDisFactorLocation = GLES20.glGetUniformLocation(getProgram(), "distanceNormalizationFactor");
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");

        // bind GL_TEXTURE_EXTERNAL_OES
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameter");
    }

    public void onOutputSizeChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

}