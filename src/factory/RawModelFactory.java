package factory;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;


import utils.MyFloatBuffer;
import entityRaw.RawModel;
//import text.Font;
//import text.LetterData;

public class RawModelFactory {
	
	private static float[] createVerticesArray(AIMesh mesh) {
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;
        
		AIVector3D.Buffer verticesAiBuffer = mesh.mVertices();
        float[] vertices = new float[verticesAiBuffer.remaining() * 3];
        int i = 0;
        while (verticesAiBuffer.hasRemaining()) {
            AIVector3D v = verticesAiBuffer.get();
            vertices[i++] = v.x();
            vertices[i++] = v.y();
            vertices[i++] = v.z();
            
            minX = Math.min(minX, v.x());
            maxX = Math.max(maxX, v.x());

            minY = Math.min(minY, v.y());
            maxY = Math.max(maxY, v.y());
            
            minZ = Math.min(minZ, v.z());
            maxZ = Math.max(maxZ, v.z());
            
        }
        
        float xScale = maxX-minX,
        		yScale = maxY-minY,
        		zScale = maxZ-minZ;
        
        float xCenter = (maxX+minX)/2;
        float yCenter = (maxY+minY)/2;
        float zCenter = (maxZ+minZ)/2;
        
        float scale = Math.max(Math.max(xScale, yScale), zScale);
        
        for (int j = 0; j < vertices.length; j+=3) {
        	
        	vertices[j] -= xCenter;
        	vertices[j] /= scale;

        	vertices[j+1] -= yCenter;
        	vertices[j+1] /= scale;
        	
        	vertices[j+2] -= zCenter;
        	vertices[j+2] /= scale;
        }
        
        return vertices;
	}
	
	private static float[] createNormalsArray(AIMesh mesh) {
		AIVector3D.Buffer normalsAiBuffer = mesh.mNormals();
        float[] normals = new float[normalsAiBuffer.remaining() * 3];
        int i = 0;
        while (normalsAiBuffer.hasRemaining()) {
            AIVector3D n = normalsAiBuffer.get();
            normals[i++] = n.x();
            normals[i++] = n.y();
            normals[i++] = n.z();
        }
        
        return normals;
	}
	
	private static float[] createTangentsArray(AIMesh mesh) {
		AIVector3D.Buffer tangentAiBuffer = mesh.mTangents();
        float[] tangents = new float[tangentAiBuffer.remaining() * 3];
        int i = 0;
        while (tangentAiBuffer.hasRemaining()) {
            AIVector3D t = tangentAiBuffer.get();
            tangents[i++] = t.x();
            tangents[i++] = t.y();
            tangents[i++] = t.z();
        }
        
        return tangents;
	}
	
	private static float[] createUVArray(AIMesh mesh) {
		AIVector3D.Buffer uvAiBuffer = mesh.mTextureCoords(0);
        float[] uvs = new float[uvAiBuffer.remaining() * 2];
        int i = 0;
        while (uvAiBuffer.hasRemaining()) {
            AIVector3D uv = uvAiBuffer.get();
            uvs[i++] = uv.x();
            uvs[i++] = uv.y();
        }
        
        return uvs;
	}
	
	
	private static int[] createIndicesArray(AIMesh mesh) {
		AIFace.Buffer faces = mesh.mFaces();
        int totalIndices = faces.remaining() * 3; // since we used aiProcess_Triangulate
        int[] indices = new int[totalIndices];

        int i = 0;
        while (faces.hasRemaining()) {
            AIFace face = faces.get();
            IntBuffer indexBuffer = face.mIndices();
            while (indexBuffer.hasRemaining()) {
                indices[i++] = indexBuffer.get();
            }
        }
        
        return indices;
	}
	
	//The Actual Loaders:
	
	public static RawModel OBJModel(String file) {
		AIScene scene = Assimp.aiImportFile(file,
                Assimp.aiProcess_Triangulate | 
                Assimp.aiProcess_JoinIdenticalVertices | 
                Assimp.aiProcess_FlipUVs);

        if (scene == null) {
            System.err.println("Failed to load OBJ");
            return null;
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
        
        // Vertices
        float[] vertices = createVerticesArray(mesh);
        
        //Normals
        float[] normals = createNormalsArray(mesh);
        
        //Indices
        int[] indices = createIndicesArray(mesh);
        
        Assimp.aiReleaseImport(scene);
        
        //Create Model Data
        
        List<MyFloatBuffer> float_buffers = new ArrayList<>();
		
        MyFloatBuffer vertexBuffer = new MyFloatBuffer(vertices, 3);
		float_buffers.add(vertexBuffer);
		
		MyFloatBuffer normalsBuffer = new MyFloatBuffer(normals, 3);
		float_buffers.add(normalsBuffer);
	
		return new RawModel(indices, float_buffers, new ArrayList<>());
	}
	
	public static RawModel quad2D(Vector2f center, Vector2f size) {
		float x = center.x;
		float y = center.y;
		float hw = size.x / 2.0f;
		float hh = size.y / 2.0f;
		
		float[] vertices = new float[] {
				x-hw,y-hh,
				x+hw,y-hh,
				x-hw,y+hh,
				x+hw,y+hh
		};
		
		int[] indices = new int[] {
				0,1,2,
				1,2,3
		};
		
		List<MyFloatBuffer> buffers = new ArrayList<>();
		
        MyFloatBuffer vertexBuffer = new MyFloatBuffer(vertices, 2);
		buffers.add(vertexBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
	public static RawModel quad2D(Vector2f center, Vector2f size, float[] uv) {
		float x = center.x;
		float y = center.y;
		float hw = size.x / 2.0f;
		float hh = size.y / 2.0f;
		
		float[] vertices = new float[] {
				x-hw,y-hh,
				x+hw,y-hh,
				x-hw,y+hh,
				x+hw,y+hh
		};
		
		int[] indices = new int[] {
				0,1,2,
				1,2,3
		};
		
		List<MyFloatBuffer> buffers = new ArrayList<>();
		
        MyFloatBuffer vertexBuffer = new MyFloatBuffer(vertices, 2);
		buffers.add(vertexBuffer);

        MyFloatBuffer uvBuffer = new MyFloatBuffer(uv, 2);
		buffers.add(uvBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
	public static RawModel screenQuad() {
		return quad2D(new Vector2f().zero(), new Vector2f(2.0f, 2.0f));
	}
	
	public static RawModel createOrbitalQuad(Vector3f north, Vector3f west, float size) {
		Vector3f ray = new Vector3f(west).normalize();
		Vector3f side1 = new Vector3f(north).normalize();
		Vector3f side2 = new Vector3f(ray).cross(side1);
		side1.mul(size/2.0f);
		side2.mul(size/2.0f);
		
		Vector3f[] vertices_vecs = new Vector3f[] {
				new Vector3f(ray).sub(side1).sub(side2),
				new Vector3f(ray).add(side1).sub(side2),
				new Vector3f(ray).sub(side1).add(side2),
				new Vector3f(ray).add(side1).add(side2)
		};
		
		float[] vertices = new float[12];
		
		for (int i = 0; i < 4; i++) {
			vertices[3*i+0] = vertices_vecs[i].x;
			vertices[3*i+1] = vertices_vecs[i].y;
			vertices[3*i+2] = vertices_vecs[i].z;
		}

		float[] uv = new float[]  {
				0.0f, 0.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f				
		};
		
		int[] indices = new int[] {
				0,1,2,
				1,2,3
		};
		
		List<MyFloatBuffer> buffers = new ArrayList<>();
		
		MyFloatBuffer vertexBuffer = new MyFloatBuffer(vertices, 3);
		buffers.add(vertexBuffer);
		
		MyFloatBuffer uvBuffer = new MyFloatBuffer(uv, 2);
		buffers.add(uvBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
	public static RawModel quadFloor(Vector3f center, Vector2f size) {
		float x = center.x;
		float y = center.y;
		float z = center.z;
		float hw = size.x / 2.0f;
		float hh = size.y / 2.0f;
		
		float[] vertices = new float[] {
				x-hw, y, z-hh,
				x+hw, y, z-hh,
				x-hw, y, z+hh,
				x+hw, y, z+hh
		};
		
		float[] normals = new float[] {
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f
		};
		
		
		int[] indices = new int[] {
				0,1,2,
				1,2,3
		};
		
		List<MyFloatBuffer> buffers = new ArrayList<>();
		
        MyFloatBuffer vertexBuffer = new MyFloatBuffer(vertices, 3);
		buffers.add(vertexBuffer);
		
		MyFloatBuffer normalsBuffer = new MyFloatBuffer(normals, 3);
		buffers.add(normalsBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
}
