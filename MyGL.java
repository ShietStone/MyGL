package org.tembins.mygl;

/**
 * MyGL is a graphics library for 3D as well as 2D rendering. It was heavily inspired
 * by OpenGL but does not work like it. The rendering completely takes place on the
 * CPU, so the quality may not be the best. MyGL is licensed under the MIT License.
 * 
 * @author Tembins
 * @version 0.0.35
 *
 */
public class MyGL {
	
	public static final String GL_VERSION = "0.0.35";
	
	public static final int GL_BYTE = 0x10000000;
	public static final int GL_INT = 0x20000000;
	public static final int GL_FLOAT = 0x40000000;
	
	public static final int GL_FRAME_BUFFER = 3;
	public static final int GL_VERTEX_BUFFER = 4;
	public static final int GL_COLOR_ATTRIB = 5;
	public static final int GL_TEXTURE_ATTRIB = 6;
	public static final int GL_DEPTH_BUFFER = 7;
	public static final int GL_TEXTURE = 8;
	
	public static final int GL_ENABLE = 9;
	public static final int GL_DISABLE = 10;
	
	public static final int GL_RGB = 11;
	public static final int GL_BGR = 12;
	public static final int GL_ARGB = 13;
	
	public static final int GL_TEXTURED = 14;
	public static final int GL_COLORED = 15;
	
	public static final int GL_PERSPECTIVE = 0x00000001;
	public static final int GL_ASPECT = 0x00000002;
	public static final int GL_BACKFACE_REMOVAL = 0x00000004;
	public static final int GL_DEPTH_TEST = 0x00000008;
	public static final int GL_OBJECT_TRANSFORM = 0x00000010;
	public static final int GL_CAMERA_TRANSFORM = 0x00000020;
	
	private static byte[][] s_byteBuffers = new byte[][]{new byte[]{0}};
	private static int[][] s_intBuffers = new int[][]{new int[]{0}};
	private static float[][] s_floatBuffers = new float[][]{new float[]{0}};
	
	private static float s_tan = (float) Math.tan(Math.toRadians(45));
	private static int s_x = 0;
	private static int s_y = 0;
	private static int s_width = 0;
	private static int s_height = 0;
	private static float s_aspectRatio = 0;
	@SuppressWarnings("unused")
	private static float s_cx, s_cy, s_cz, s_cxc, s_cxs, s_cyc, s_cys, s_czc, s_czs;
	private static float s_ox, s_oy, s_oz, s_oxc, s_oxs, s_oyc, s_oys, s_ozc, s_ozs;
	
	private static int s_flags = 0;
	
	private static int s_frameBuffer = 0;
	private static int s_vertexBuffer = 0;
	private static int s_colorAttrib = 0;
	private static int s_textureAttrib = 0;
	private static int s_depthBuffer = 0;
	@SuppressWarnings("unused")
	private static int s_texture = 0;
	
	@SuppressWarnings("unused")
	private static int s_colorFormat = 0;
	
	static {
		System.out.println("[MyGL] MyGL v" + GL_VERSION + " active");
	}
	
	/**
	 * Generates a buffer of a specific type.
	 * 
	 * @param type The type of the buffer to generate.
	 * @return A number used as ID for the generated buffer.
	 * 
	 */
	public static int glGenBuffer(int type) {
		if(type == GL_BYTE) {
			byte[][] newArray = new byte[s_byteBuffers.length + 1][];
			
			for(int i = 0; i < s_byteBuffers.length; i++)
				newArray[i] = s_byteBuffers[i];
			
			s_byteBuffers = newArray;
			return (newArray.length - 1) | GL_BYTE;
		} else if(type == GL_INT) {
			int[][] newArray = new int[s_intBuffers.length + 1][];
			
			for(int i = 0; i < s_intBuffers.length; i++)
				newArray[i] = s_intBuffers[i];
			
			s_intBuffers = newArray;
			return (newArray.length - 1) | GL_INT;
		} else if(type == GL_FLOAT) {
			float[][] newArray = new float[s_floatBuffers.length + 1][];
			
			for(int i = 0; i < s_floatBuffers.length; i++)
				newArray[i] = s_floatBuffers[i];
			
			s_floatBuffers = newArray;
			return (newArray.length - 1) | GL_FLOAT;
		}
		
		return -1;
	}
	
	/**
	 * Initializes a buffer to a specific length.
	 * 
	 * @param length How many items should fit into the buffer.
	 * @param value The value to fill the buffer with.
	 * @param buffer The buffer to initialize.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glInitBuffer(int length, byte value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		if(type == GL_BYTE) {
			byte[] newArray = new byte[length];
			for(int i = 0; i < length; i++)
				newArray[i] = value;
			s_byteBuffers[nBuffer] = newArray;
			return 0;
		} else if(type == GL_INT) {
			int[] newArray = new int[length];
			int iValue = (int) value;
			for(int i = 0; i < length; i++)
				newArray[i] = (iValue << 24) | (iValue << 16) | (iValue << 8) | iValue;
			s_intBuffers[nBuffer] = newArray;
			return 0;
		} else if(type == GL_FLOAT) {
			float[] newArray = new float[length];
			int iValue = (int) value;
			for(int i = 0; i < length; i++)
				newArray[i] = Float.intBitsToFloat((iValue << 24) | (iValue << 16) | (iValue << 8) | iValue);
			s_floatBuffers[nBuffer] = newArray;
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Initializes a buffer to a specific length.
	 * 
	 * @param length How many items should fit into the buffer.
	 * @param value The value to fill the buffer with.
	 * @param buffer The buffer to initialize.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glInitBuffer(int length, int value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE) {
			byte[] newArray = new byte[length * 4];
			byte[] values = new byte[] {(byte) ((value >> 24) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 8) & 255), (byte) (value & 255)};
			
			int inInt = 0;
			for(int i = 0; i < newArray.length; i++) {
				newArray[i] = values[inInt];
				inInt++;
				if(inInt == 4)
					inInt = 0;
			}
			
			s_byteBuffers[nBuffer] = newArray;
			return 0;
		} else if(type == GL_INT) {
			int[] newArray = new int[length];

			for(int i = 0; i < length; i++)
				newArray[i] = value;
			
			s_intBuffers[buffer] = newArray;
			return 0;
		} else if(type == GL_FLOAT) {
			float[] newArray = new float[length];

			for(int i = 0; i < length; i++)
				newArray[i] = Float.intBitsToFloat(value);
			
			s_floatBuffers[nBuffer] = newArray;
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Initializes a buffer to a specific length.
	 * 
	 * @param length How many items should fit into the buffer.
	 * @param value The value to fill the buffer with.
	 * @param buffer The buffer to initialize.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glInitBuffer(int length, float value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE) {
			byte[] newArray = new byte[length * 4];
			int iValue = Float.floatToIntBits(value);
			byte[] values = new byte[] {(byte) ((iValue >> 24) & 255), (byte) ((iValue >> 16) & 255), (byte) ((iValue >> 8) & 255), (byte) (iValue & 255)};
			
			int inInt = 0;
			for(int i = 0; i < newArray.length; i++) {
				newArray[i] = values[inInt];
				inInt++;
				if(inInt == 4)
					inInt = 0;
			}
			
			s_byteBuffers[nBuffer] = newArray;
			return 0;
		} else if(type == GL_INT) {
			int[] newArray = new int[length];

			for(int i = 0; i < length; i++)
				newArray[i] = Float.floatToIntBits(value);
			
			s_intBuffers[buffer] = newArray;
			return 0;
		} else if(type == GL_FLOAT) {
			float[] newArray = new float[length];

			for(int i = 0; i < length; i++)
				newArray[i] = value;
			
			s_floatBuffers[nBuffer] = newArray;
			return 0;
		}
		
		return -1;
	}

	/**
	 * Links a buffer to the data contained by the GLBuffer.
	 * 
	 * @param content The data to link.
	 * @param buffer The buffer to link the data to.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glContent(GLBuffer content, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;

		if(type == GL_BYTE) {
			s_byteBuffers[nBuffer] = content.getAsByteArray();
			return 0;
		} else if(type == GL_INT) {
			s_intBuffers[nBuffer] = content.getAsIntArray();
			return 0;
		} else if(type == GL_FLOAT) {
			s_floatBuffers[nBuffer] = content.getAsFloatArray();
			return 0;
		}
		
		return -1;
	}

	/**
	 * Creates an access to a buffer.
	 * 
	 * @param buffer The buffer to access.
	 * @return A GLBuffer containing the data.
	 * 
	 */
	public static GLBuffer glContent(int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE)
			return new GLBuffer(s_byteBuffers[nBuffer]);
		else if(type == GL_INT)
			return new GLBuffer(s_intBuffers[nBuffer]);
		else if(type == GL_FLOAT)
			return new GLBuffer(s_floatBuffers[nBuffer]);
		
		return null;
	}

	/**
	 * Creates the Viewport of the screen. (Does not modify depth buffers)
	 * 
	 * @param x The x coordinate of the viewport.
	 * @param y The y coordinate of the viewport.
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glViewport(int x, int y, int width, int height) {
		s_x = x;
		s_y = y;
		s_width = width;
		s_height = height;
		s_aspectRatio = (float) width / (float) height;
		return 0;
	}
	
	/**
	 * Draws and transforms primitives specified by bound buffers.
	 * 
	 * @param drawMode The mode used for drawing.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glDraw(int drawMode) {
		float[] vertexBuffer = s_floatBuffers[s_vertexBuffer];
		float[] dataBuffer = drawMode == GL_TEXTURED ? s_floatBuffers[s_textureAttrib] : s_floatBuffers[s_colorAttrib];
		int primitiveAmount = vertexBuffer.length / 9;

		int primitiveIndex = 0;
		int dataIndex = 0;
		for(int i = 0; i < primitiveAmount; i++) {
			float[] AVertex = new float[3];
			float[] BVertex = new float[3];
			float[] CVertex = new float[3];
			float[] AData = new float[3];
			float[] BData = new float[3];
			float[] CData = new float[3];
			
			AVertex[0] = vertexBuffer[primitiveIndex++];
			AVertex[1] = vertexBuffer[primitiveIndex++];
			AVertex[2] = vertexBuffer[primitiveIndex++];
			BVertex[0] = vertexBuffer[primitiveIndex++];
			BVertex[1] = vertexBuffer[primitiveIndex++];
			BVertex[2] = vertexBuffer[primitiveIndex++];
			CVertex[0] = vertexBuffer[primitiveIndex++];
			CVertex[1] = vertexBuffer[primitiveIndex++];
			CVertex[2] = vertexBuffer[primitiveIndex++];

			if(drawMode == GL_TEXTURED) {
				AData[0] = dataBuffer[dataIndex++];
				BData[1] = dataBuffer[dataIndex++];
				CData[2] = 0;
				AData[0] = dataBuffer[dataIndex++];
				BData[1] = dataBuffer[dataIndex++];
				CData[2] = 0;
				AData[0] = dataBuffer[dataIndex++];
				BData[1] = dataBuffer[dataIndex++];
				CData[2] = 0;
			} else {
				AData[0] = dataBuffer[dataIndex++];
				AData[1] = dataBuffer[dataIndex++];
				AData[2] = dataBuffer[dataIndex++];
				BData[0] = dataBuffer[dataIndex++];
				BData[1] = dataBuffer[dataIndex++];
				BData[2] = dataBuffer[dataIndex++];
				CData[0] = dataBuffer[dataIndex++];
				CData[1] = dataBuffer[dataIndex++];
				CData[2] = dataBuffer[dataIndex++];
			}

			if((s_flags & GL_OBJECT_TRANSFORM) == GL_OBJECT_TRANSFORM) {
				float[] nAVertex = new float[]{AVertex[0], AVertex[1], AVertex[2]};
				float[] nBVertex = new float[]{BVertex[0], BVertex[1], BVertex[2]};
				float[] nCVertex = new float[]{CVertex[0], CVertex[1], CVertex[2]};
				
				nAVertex[1] = AVertex[1] * s_oxc + AVertex[2] * -s_oxs;
				nAVertex[2] = AVertex[1] * s_oxs + AVertex[2] * s_oxc;
				nBVertex[1] = BVertex[1] * s_oxc + BVertex[2] * -s_oxs;
				nBVertex[2] = BVertex[1] * s_oxs + BVertex[2] * s_oxc;
				nCVertex[1] = CVertex[1] * s_oxc + CVertex[2] * -s_oxs;
				nCVertex[2] = CVertex[1] * s_oxs + CVertex[2] * s_oxc;
				
				AVertex = nAVertex;
				BVertex = nBVertex;
				CVertex = nCVertex;
				nAVertex = new float[]{AVertex[0], AVertex[1], AVertex[2]};
				nBVertex = new float[]{BVertex[0], BVertex[1], BVertex[2]};
				nCVertex = new float[]{CVertex[0], CVertex[1], CVertex[2]};
				
				nAVertex[0] = AVertex[0] * s_oyc + AVertex[2] * s_oys;
				nAVertex[2] = AVertex[0] * -s_oys + AVertex[2] * s_oyc;
				nBVertex[0] = BVertex[0] * s_oyc + BVertex[2] * s_oys;
				nBVertex[2] = BVertex[0] * -s_oys + BVertex[2] * s_oyc;
				nCVertex[0] = CVertex[0] * s_oyc + CVertex[2] * s_oys;
				nCVertex[2] = CVertex[0] * -s_oys + CVertex[2] * s_oyc;
				
				AVertex = nAVertex;
				BVertex = nBVertex;
				CVertex = nCVertex;
				nAVertex = new float[]{AVertex[0], AVertex[1], AVertex[2]};
				nBVertex = new float[]{BVertex[0], BVertex[1], BVertex[2]};
				nCVertex = new float[]{CVertex[0], CVertex[1], CVertex[2]};
				
				nAVertex[0] = AVertex[0] * s_ozc + AVertex[1] * -s_ozs;
				nAVertex[1] = AVertex[0] * s_ozs + AVertex[1] * s_ozc;
				nBVertex[0] = BVertex[0] * s_ozc + BVertex[1] * -s_ozs;
				nBVertex[1] = BVertex[0] * s_ozs + BVertex[1] * s_ozc;
				nCVertex[0] = CVertex[0] * s_ozc + CVertex[1] * -s_ozs;
				nCVertex[1] = CVertex[0] * s_ozs + CVertex[1] * s_ozc;
				
				nAVertex[0] += s_ox;
				nAVertex[1] += s_oy;
				nAVertex[2] += s_oz;
				nBVertex[0] += s_ox;
				nBVertex[1] += s_oy;
				nBVertex[2] += s_oz;
				nCVertex[0] += s_ox;
				nCVertex[1] += s_oy;
				nCVertex[2] += s_oz;
				
				AVertex = nAVertex;
				BVertex = nBVertex;
				CVertex = nCVertex;
			}
			
			if((s_flags & GL_CAMERA_TRANSFORM) == GL_CAMERA_TRANSFORM) {
				
			}
			
			float near = 0.5f;
			if(AVertex[2] <= near || BVertex[2] <= near || CVertex[2] <= near)
				continue;
			
			float[] posV1 = null;
			@SuppressWarnings("unused")
			float[] posV2 = null;
			float[] negV1 = null;
			float[] negV2 = null;
			float[] posD1 = null;
			@SuppressWarnings("unused")
			float[] posD2 = null;
			float[] negD1 = null;
			float[] negD2 = null;
			int posI1 = 0;
			@SuppressWarnings("unused")
			int posI2 = 0;
			int negI1 = 0;
			int negI2 = 0;
			int neg = 0;
			
			if(AVertex[2] <= near) {
				neg++;
				negV1 = AVertex;
				negD1 = AData;
				negI1 = 0;
			} else {
				posV1 = AVertex;
				posD1 = AData;
				posI1 = 0;
			}
			
			if(BVertex[2] <= near) {
				if(neg == 1) {
					negV2 = BVertex;
					negD2 = BData;
					negI2 = 1;
				} else {
					negV1 = BVertex;
					negD1 = BData;
					negI1 = 1;
				}
				neg++;
			} else {
				if(neg == 0) {
					posV1 = BVertex;
					posD1 = BData;
					posI1 = 1;
				} else {
					posV2 = BVertex;
					posD2 = BData;
					posI2 = 1;
				}
			}
			
			if(CVertex[2] <= near) {
				if(neg == 2) {
					continue;
				} else if(neg == 1) {
					negV2 = CVertex;
					negD2 = CData;
					negI2 = 2;
				} else {
					negV1 = CVertex;
					negD1 = CData;
					negI1 = 2;
				}
				
				neg++;
			} else {
				if(neg == 0) {
					pglTriangle(AVertex, BVertex, CVertex, AData, BData, CData, drawMode);
					continue;
				} else if(neg == 1){
					posV2 = CVertex;
					posD2 = CData;
					posI2 = 2;
				} else {
					posV1 = CVertex;
					posD1 = CData;
					posI1 = 2;
				}
			}

			if(neg == 1) {
				
			} else {
				float[] toN1V = new float[] {negV1[0] - posV1[0], negV1[1] - posV1[1], negV1[2] - posV1[2]};
				float[] toN2V = new float[] {negV2[0] - posV1[0], negV2[1] - posV1[1], negV2[2] - posV1[2]};
				float[] toN1D = new float[] {negD1[0] - posD1[0], negD1[1] - posD1[1], negD1[2] - posD1[2]};
				float[] toN2D = new float[] {negD2[0] - posD1[0], negD2[1] - posD1[1], negD2[2] - posD1[2]};
				float toN1Fac = near / toN1V[2];
				float toN2Fac = near / toN2V[2];
				
				toN1V[0] = toN1V[0] * toN1Fac + posV1[0];
				toN1V[1] = toN1V[1] * toN1Fac + posV1[1];
				toN1V[2] = near;
				
				toN1D[0] = toN1D[0] * toN1Fac + posD1[0];
				toN1D[1] = toN1D[1] * toN1Fac + posD1[1];
				toN1D[2] = near;
				
				toN2V[0] = toN2V[0]* toN2Fac + posV1[0];
				toN2V[1] = toN2V[1] * toN2Fac + posV1[1];
				toN2V[2] = near;
				
				toN2D[0] = toN2D[0] * toN2Fac + posD1[0];
				toN2D[1] = toN2D[1] * toN2Fac + posD1[1];
				toN2D[2] = near;
				
				if(posI1 == 0) {AVertex = posV1; AData = posD1;}
				if(posI1 == 1) {BVertex = posV1; BData = posD1;}
				if(posI1 == 2) {CVertex = posV1; CData = posD1;}
				if(negI1 == 0) {AVertex = toN1V; AData = toN1D;}
				if(negI1 == 1) {BVertex = toN1V; BData = toN1D;}
				if(negI1 == 2) {CVertex = toN1V; CData = toN1D;}
				if(negI2 == 0) {AVertex = toN2V; AData = toN2D;}
				if(negI2 == 1) {BVertex = toN2V; BData = toN2D;}
				if(negI2 == 2) {CVertex = toN2V; CData = toN2D;}
				
				pglTriangle(AVertex, BVertex, CVertex, AData, BData, CData, drawMode);
			}
			
			//pglTriangle(AVertex, BVertex, CVertex, AData, BData, CData, drawMode);
		}
		
		return 0;
	}
	
	private static int pglTriangle(float[] AVertex, float BVertex[] , float[] CVertex, float[] AData, float[] BData, float[] CData, int drawMode) {
		float[] nAVertex = null;
		float[] nBVertex = null;
		float[] nCVertex = null;
		
		if((s_flags & GL_ASPECT) == GL_ASPECT && (s_flags & GL_PERSPECTIVE) == GL_PERSPECTIVE) {
			nAVertex = new float[]{(AVertex[0] / (AVertex[2] * s_tan) + 1) * 0.5f * s_width, (AVertex[1] / -(AVertex[2] * s_tan) * s_aspectRatio + 1) * 0.5f * s_height, 1f / AVertex[2]};
			nBVertex = new float[]{(BVertex[0] / (BVertex[2] * s_tan) + 1) * 0.5f * s_width, (BVertex[1] / -(BVertex[2] * s_tan) * s_aspectRatio + 1) * 0.5f * s_height, 1f / BVertex[2]};
			nCVertex = new float[]{(CVertex[0] / (CVertex[2] * s_tan) + 1) * 0.5f * s_width, (CVertex[1] / -(CVertex[2] * s_tan) * s_aspectRatio + 1) * 0.5f  * s_height, 1f / CVertex[2]};
		} else if((s_flags & GL_ASPECT) == GL_ASPECT && (s_flags & GL_PERSPECTIVE) != GL_PERSPECTIVE) {
			nAVertex = new float[]{(AVertex[0] + 1) * 0.5f * s_width, (-AVertex[1] * s_aspectRatio + 1) * 0.5f * s_height, 1f / AVertex[2]};
			nBVertex = new float[]{(BVertex[0] + 1) * 0.5f * s_width, (-BVertex[1] * s_aspectRatio + 1) * 0.5f * s_height, 1f / BVertex[2]};
			nCVertex = new float[]{(CVertex[0] + 1) * 0.5f * s_width, (-CVertex[1] * s_aspectRatio + 1) * 0.5f  * s_height, 1f / CVertex[2]};
		} else if((s_flags & GL_ASPECT) != GL_ASPECT && (s_flags & GL_PERSPECTIVE) == GL_PERSPECTIVE) {
			nAVertex = new float[]{(AVertex[0] / (AVertex[2] * s_tan) + 1) * 0.5f * s_width, (AVertex[1] / -(AVertex[2] * s_tan) + 1) * 0.5f * s_height, 1f / AVertex[2]};
			nBVertex = new float[]{(BVertex[0] / (BVertex[2] * s_tan) + 1) * 0.5f * s_width, (BVertex[1] / -(BVertex[2] * s_tan) + 1) * 0.5f * s_height, 1f / BVertex[2]};
			nCVertex = new float[]{(CVertex[0] / (CVertex[2] * s_tan) + 1) * 0.5f * s_width, (CVertex[1] / -(CVertex[2] * s_tan) + 1) * 0.5f  * s_height, 1f / CVertex[2]};
		} else {
			nAVertex = new float[]{(AVertex[0] + 1) * 0.5f * s_width, (-AVertex[1] + 1) * 0.5f * s_height, 1f / AVertex[2]};
			nBVertex = new float[]{(BVertex[0] + 1) * 0.5f * s_width, (-BVertex[1] + 1) * 0.5f * s_height, 1f / BVertex[2]};
			nCVertex = new float[]{(CVertex[0] + 1) * 0.5f * s_width, (-CVertex[1] + 1) * 0.5f  * s_height, 1f / CVertex[2]};
		}
		
		AData[0] *= nAVertex[2];
		AData[1] *= nAVertex[2];
		AData[2] *= nAVertex[2];
		BData[0] *= nBVertex[2];
		BData[1] *= nBVertex[2];
		BData[2] *= nBVertex[2];
		CData[0] *= nCVertex[2];
		CData[1] *= nCVertex[2];
		CData[2] *= nCVertex[2];
		
		int minX = (int) Math.min(nAVertex[0], Math.min(nBVertex[0], nCVertex[0]));
		int minY = (int) Math.min(nAVertex[1], Math.min(nBVertex[1], nCVertex[1]));
		int maxX = (int) (Math.max(nAVertex[0], Math.max(nBVertex[0], nCVertex[0])) + 0.5f);
		int maxY = (int) (Math.max(nAVertex[1], Math.max(nBVertex[1], nCVertex[1])) + 0.5f);
		minX = Math.max(0, Math.min(s_width - 1, minX));
		maxX = Math.max(0, Math.min(s_width - 1, maxX));
		minY = Math.max(0, Math.min(s_height - 1, minY));
		maxY = Math.max(0, Math.min(s_height - 1, maxY));

		for(int y = minY; y <= maxY; y++)
			for(int x = minX; x <= maxX; x++) {
				int edge0 = (int) ((x - nAVertex[0]) * (nBVertex[1] - nAVertex[1]) - (y - nAVertex[1]) * (nBVertex[0] - nAVertex[0]));
				int edge1 = (int) ((x - nBVertex[0]) * (nCVertex[1] - nBVertex[1]) - (y - nBVertex[1]) * (nCVertex[0] - nBVertex[0]));
				int edge2 = (int) ((x - nCVertex[0]) * (nAVertex[1] - nCVertex[1]) - (y - nCVertex[1]) * (nAVertex[0] - nCVertex[0]));

				float b0 = (nBVertex[1] - nCVertex[1]) * (nAVertex[0] - nCVertex[0]) + (nCVertex[0] - nBVertex[0]) * (nAVertex[1] - nCVertex[1]);
				float b1 = ((nBVertex[1] - nCVertex[1]) * (x - nCVertex[0]) + (nCVertex[0] - nBVertex[0]) * (y - nCVertex[1])) / b0;
				float b2 = ((nCVertex[1] - nAVertex[1]) * (x - nCVertex[0]) + (nAVertex[0] - nCVertex[0]) * (y - nCVertex[1])) / b0;
				float b3 = 1f - b1 - b2;

				if((edge0 >= 0 && edge1 >= 0 && edge2 >= 0) || (edge0 <= 0 && edge1 <= 0 && edge2 <= 0)) {
					int index = ((y + s_y) * s_width + x + s_x) * 3;
					boolean dephtTest = true;
					float nZ = 1f / (nAVertex[2] * b1 + nBVertex[2] * b2 + nCVertex[2] * b3);
					
					if((s_flags & GL_DEPTH_TEST) == GL_DEPTH_TEST) {
						float dephtValue = s_floatBuffers[s_depthBuffer][y * s_width + x];
						if(dephtValue < nZ)
							dephtTest = false;
						else
							s_floatBuffers[s_depthBuffer][y * s_width + x] = nZ;
					}
					
					if(dephtTest) {
						if(drawMode == GL_COLORED) {
							int red = (int) ((AData[0] * nZ * b1 + BData[0] * nZ * b2 + CData[0] * nZ * b3) * 255);			
							int green = (int) ((AData[1] * nZ * b1 + BData[1] * nZ * b2 + CData[1] * nZ * b3) * 255);
							int blue = (int) ((AData[2] * nZ * b1 + BData[2] * nZ * b2 + CData[2] * nZ * b3) * 255);
						
							s_byteBuffers[s_frameBuffer][index] = (byte) blue;
							s_byteBuffers[s_frameBuffer][index + 1] = (byte) green;
							s_byteBuffers[s_frameBuffer][index + 2] = (byte) red;
						} else {
						
						}
					}
				}
				
			}
	
		return 0;
	}
	
	/**
	 * Accesses the flags register.
	 * 
	 * @param mode The mode to access the flags register.
	 * @param flags Specified flags.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glFlags(int mode, int flags) {
		if(mode == GL_ENABLE) {
			s_flags = s_flags | flags; 
			return 0;
		} else if(mode == GL_DISABLE){
			s_flags = s_flags & (~flags);
			return 0;
		}
		
		return -1;
	}

	/**
	 * Clears a buffer to a specified value.
	 * 
	 * @param value The value to write into the buffer.
	 * @param buffer The buffer to clear.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glClear(byte value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE) {
			byte[] tBuffer = s_byteBuffers[nBuffer];
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = value;
			return 0;
		} else if(type == GL_INT) {
			int[] tBuffer = s_intBuffers[nBuffer];
			int iValue = (int) value;
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = (iValue << 24) | (iValue << 16) | (iValue << 8) | iValue;
			return 0;
		} else if(type == GL_FLOAT) {
			float[] tBuffer = s_floatBuffers[nBuffer];
			int iValue = (int) value;
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = Float.intBitsToFloat((iValue << 24) | (iValue << 16) | (iValue << 8) | iValue);
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Clears a buffer to a specified value.
	 * 
	 * @param value The value to write into the buffer.
	 * @param buffer The buffer to clear.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glClear(int value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE) {
			byte[] tBuffer = s_byteBuffers[nBuffer];
			byte[] values = new byte[] {(byte) ((value >> 24) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 8) & 255), (byte) (value & 255)};
			
			int inInt = 0;
			for(int i = 0; i < tBuffer.length; i++) {
				tBuffer[i] = values[inInt];
				inInt++;
				if(inInt == 4)
					inInt = 0;
			}
			return 0;
		} else if(type == GL_INT) {
			int[] tBuffer = s_intBuffers[nBuffer];
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = value;
			return 0;
		} else if(type == GL_FLOAT) {
			float[] tBuffer = s_floatBuffers[nBuffer];
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = Float.intBitsToFloat(value);
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Clears a buffer to a specified value.
	 * 
	 * @param value The value to write into the buffer.
	 * @param buffer The buffer to clear.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glClear(float value, int buffer) {
		int type = buffer & 0xF0000000;
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(type == GL_BYTE) {
			byte[] tBuffer = s_byteBuffers[nBuffer];
			int iValue = Float.floatToIntBits(value);
			byte[] values = new byte[] {(byte) ((iValue >> 24) & 255), (byte) ((iValue >> 16) & 255), (byte) ((iValue >> 8) & 255), (byte) (iValue & 255)};
			
			int inInt = 0;
			for(int i = 0; i < tBuffer.length; i++) {
				tBuffer[i] = values[inInt];
				inInt++;
				if(inInt == 4)
					inInt = 0;
			}
			return 0;
		} else if(type == GL_INT) {
			int[] tBuffer = s_intBuffers[nBuffer];
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = Float.floatToIntBits(value);
			return 0;
		} else if(type == GL_FLOAT) {
			float[] tBuffer = s_floatBuffers[nBuffer];
			
			for(int i = 0; i < tBuffer.length; i++)
				tBuffer[i] = value;
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Binds a buffer to a target.
	 * 
	 * @param target The target to bind the buffer to.
	 * @param buffer The buffer to bind.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glBind(int target, int buffer) {
		int nBuffer = buffer & 0x0FFFFFFF;
		
		if(target == GL_FRAME_BUFFER) {
			s_frameBuffer = nBuffer;
			return 0;
		} else if(target == GL_VERTEX_BUFFER) {
			s_vertexBuffer = nBuffer;
			return 0;
		} else if(target == GL_COLOR_ATTRIB) {
			s_colorAttrib = nBuffer;
			return 0;
		} else if(target == GL_TEXTURE_ATTRIB) {
			s_textureAttrib = nBuffer;
			return 0;
		} else if(target == GL_DEPTH_BUFFER) {
			s_depthBuffer = nBuffer;
			return 0;
		} else if(target == GL_TEXTURE) {
			s_texture = nBuffer;
			return 0;
		}
		return -1;
	}

	/**
	 * Sets the format how colors are represented. Default is BGR.
	 * 
	 * @param format The specific format.
	 * @return A negative number if an error occurred.
	 * @deprecated Not implemented yet.
	 * 
	 */
	@Deprecated
	public static int glColorFormat(int format) {
		s_colorFormat = format;
		return 0;
	}
	
	/**
	 * Sets the values for translation in a transformation pipeline.
	 * 
	 * @param target The transformation pipeline.
	 * @param x The x value.
	 * @param y The y value.
	 * @param z The z value.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glTranslate(int target, float x, float y, float z) {
		if(target == GL_CAMERA_TRANSFORM) {
			s_cx = x;
			s_cy = y;
			s_cz = z;
		} else {
			s_ox = x;
			s_oy = y;
			s_oz = z;
		}
		return 0;
	}
	
	/**
	 * Sets the values for rotation in a transformation pipeline.
	 * 
	 * @param target The transformation pipeline.
	 * @param pitch The pitch value.
	 * @param yaw The yaw value.
	 * @param roll The roll value.
	 * @return A negative number if an error occurred.
	 * 
	 */
	public static int glRotate(int target, float pitch, float yaw, float roll) {
		if(target == GL_CAMERA_TRANSFORM) {
			s_cxc = (float) Math.cos(Math.toRadians(pitch));
			s_cxs = (float) Math.sin(Math.toRadians(pitch));
			
			s_cyc = (float) Math.cos(Math.toRadians(yaw));
			s_cys = (float) Math.sin(Math.toRadians(yaw));
			
			s_czc = (float) Math.cos(Math.toRadians(roll));
			s_czs = (float) Math.sin(Math.toRadians(roll));
		} else {
			s_oxc = (float) Math.cos(Math.toRadians(pitch));
			s_oxs = (float) Math.sin(Math.toRadians(pitch));
			
			s_oyc = (float) Math.cos(Math.toRadians(yaw));
			s_oys = (float) Math.sin(Math.toRadians(yaw));
			
			s_ozc = (float) Math.cos(Math.toRadians(roll));
			s_ozs = (float) Math.sin(Math.toRadians(roll));
		}
		
		return 0;
	}
	
	/**
	 * A class representing a buffer. The buffer can be interpreted as different primitives.
	 * Linking does not work with different primitves.
	 * 
	 */
	public static class GLBuffer {
		private int m_type;
		private byte[] m_byteArray = null;
		private int[] m_intArray = null;
		private float[] m_floatArray = null;
		
		public GLBuffer(byte[] array) {
			m_byteArray = array;
			m_type = GL_BYTE;
		}
		
		public GLBuffer(int[] array) {
			m_intArray = array;
			m_type = GL_INT;
		}
		
		public GLBuffer(float[] array) {
			m_floatArray = array;
			m_type = GL_FLOAT;
		}
		
		public int getType() {
			return m_type;
		}
		
		public byte[] getAsByteArray() {
			if(m_type == GL_BYTE)
				return m_byteArray;
			
			if(m_type == GL_INT) {
				byte[] byteArray = new byte[m_intArray.length * 4];
				int index = 0;
				
				for(int i = 0; i < m_intArray.length; i++) {
					byteArray[index++] = (byte) ((m_intArray[i] >> 24) & 0x000000FF);
					byteArray[index++] = (byte) ((m_intArray[i] >> 16) & 0x000000FF);
					byteArray[index++] = (byte) ((m_intArray[i] >> 8) & 0x000000FF);
					byteArray[index++] = (byte) (m_intArray[i] & 0x000000FF);
				}
				
				return byteArray;
			}
			
			if(m_type == GL_FLOAT) {
				byte[] byteArray = new byte[m_floatArray.length * 4];
				int index = 0;
				
				for(int i = 0; i < m_floatArray.length; i++) {
					int integer = Float.floatToIntBits(m_floatArray[i]);
					
					byteArray[index++] = (byte) ((integer >> 24) & 0x000000FF);
					byteArray[index++] = (byte) ((integer >> 16) & 0x000000FF);
					byteArray[index++] = (byte) ((integer >> 8) & 0x000000FF);
					byteArray[index++] = (byte) (integer & 0x000000FF);
				}
				
				return byteArray;
			}
			
			return null;
		}
		
		public int[] getAsIntArray() {
			if(m_type == GL_BYTE) {
				int[] intArray = new int[m_byteArray.length / 4];
				int index = 0;
				
				for(int i = 0; i < intArray.length; i++) {
					intArray[i] = ((int) m_byteArray[index] << 24) | ((int) m_byteArray[index + 1] << 16) | ((int) m_byteArray[index + 2] << 8) | ((int) m_byteArray[index + 3]);
					index += 4;
				}
				
				return intArray;
			}
			
			if(m_type == GL_INT)
				return m_intArray;
			
			if(m_type == GL_FLOAT) {
				int[] intArray = new int[m_floatArray.length];
				
				for(int i = 0; i < m_floatArray.length; i++)
					intArray[i] = Float.floatToIntBits(m_floatArray[i]);
				
				return intArray;
			}
			
			return null;
		}
		
		public float[] getAsFloatArray() {
			if(m_type == GL_BYTE) {
				float[] intArray = new float[m_byteArray.length / 4];
				int index = 0;
				
				for(int i = 0; i < intArray.length; i++) {
					intArray[i] = Float.intBitsToFloat(((int) m_byteArray[index] << 24) | ((int) m_byteArray[index + 1] << 16) | ((int) m_byteArray[index + 2] << 8) | ((int) m_byteArray[index + 3]));
					index += 4;
				}
				
				return intArray;
			}
			
			if(m_type == GL_INT) {
				float[] floatArray = new float[m_intArray.length];
				
				for(int i = 0; i < floatArray.length; i++)
					floatArray[i] = Float.intBitsToFloat(m_intArray[i]);
				
				return floatArray;
			}
			
			if(m_type == GL_FLOAT)
				return m_floatArray;
			
			return null;
		}
	}
	
}

