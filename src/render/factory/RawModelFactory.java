package render.factory;

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

import render.raw.buffers.RawFloatBuffer;
import render.raw.components.RawModel;

public class RawModelFactory {
	
	private static float[] createVerticesArray(AIMesh mesh) {
	
	    float minX = Float.POSITIVE_INFINITY;
	    float minY = Float.POSITIVE_INFINITY;
	    float minZ = Float.POSITIVE_INFINITY;
	
	    float maxX = Float.NEGATIVE_INFINITY;
	    float maxY = Float.NEGATIVE_INFINITY;
	    float maxZ = Float.NEGATIVE_INFINITY;
	
	    int vertexCount = mesh.mNumVertices();
	
	    float[] vertices = new float[vertexCount * 3];
	
	    for (int i = 0; i < vertexCount; i++) {
	
	        AIVector3D v = mesh.mVertices().get(i);
	
	        int idx = i * 3;
	
	        vertices[idx]     = v.x();
	        vertices[idx + 1] = v.y();
	        vertices[idx + 2] = v.z();
	
	        minX = Math.min(minX, v.x());
	        maxX = Math.max(maxX, v.x());
	
	        minY = Math.min(minY, v.y());
	        maxY = Math.max(maxY, v.y());
	
	        minZ = Math.min(minZ, v.z());
	        maxZ = Math.max(maxZ, v.z());
	    }
	
	    float xScale = maxX - minX;
	    float yScale = maxY - minY;
	    float zScale = maxZ - minZ;
	
	    float xCenter = (maxX + minX) * 0.5f;
	    float yCenter = (maxY + minY) * 0.5f;
	    float zCenter = (maxZ + minZ) * 0.5f;
	
	    float scale = Math.max(Math.max(xScale, yScale), zScale);
	
	    for (int i = 0; i < vertices.length; i += 3) {
	
	        vertices[i]     = (vertices[i]     - xCenter) / scale;
	        vertices[i + 1] = (vertices[i + 1] - yCenter) / scale;
	        vertices[i + 2] = (vertices[i + 2] - zCenter) / scale;
	    }
	
	    return vertices;
	}
	
	private static float[] createNormalsArray(AIMesh mesh) {
	
	    int vertexCount = mesh.mNumVertices();
	
	    float[] normals = new float[vertexCount * 3];
	
	    AIVector3D.Buffer normalsBuffer = mesh.mNormals();
	
	    for (int i = 0; i < vertexCount; i++) {
	
	        AIVector3D n = normalsBuffer.get(i);
	
	        int idx = i * 3;
	
	        normals[idx]     = n.x();
	        normals[idx + 1] = n.y();
	        normals[idx + 2] = n.z();
	    }
	
	    return normals;
	}

	private static float[] createTangentsArray(AIMesh mesh) {

	    AIVector3D.Buffer tangentBuffer = mesh.mTangents();
	
	    int vertexCount = mesh.mNumVertices();
	
	    float[] tangents = new float[vertexCount * 3];
	
	    if (tangentBuffer == null) {
	        return tangents; // all zeros if missing
	    }
	
	    for (int i = 0; i < vertexCount; i++) {
	
	        AIVector3D t = tangentBuffer.get(i);
	
	        int idx = i * 3;
	
	        tangents[idx]     = t.x();
	        tangents[idx + 1] = t.y();
	        tangents[idx + 2] = t.z();
	    }
	
	    return tangents;
	}
	
	private static float[] createUVArray(AIMesh mesh) {

	    AIVector3D.Buffer uvBuffer = mesh.mTextureCoords(0);

	    int vertexCount = mesh.mNumVertices();

	    float[] uvs = new float[vertexCount * 2];

	    if (uvBuffer == null) {
	        return uvs; // all 0,0 UVs
	    }

	    for (int i = 0; i < vertexCount; i++) {

	        AIVector3D uv = uvBuffer.get(i);

	        int idx = i * 2;

	        uvs[idx]     = uv.x();
	        uvs[idx + 1] = uv.y();
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
		return OBJModel(file, false, false);
	}
	
	public static RawModel OBJModel(String file, boolean with_uv, boolean with_tangents) {
		int flags =
		        Assimp.aiProcess_Triangulate |
		        Assimp.aiProcess_JoinIdenticalVertices;
		if (with_tangents) {
		    flags |= Assimp.aiProcess_CalcTangentSpace;
		}

		AIScene scene = Assimp.aiImportFile(file, flags);

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
        
        float[] uvs = {};
        float[] tangents = {};
        
        if (with_tangents) {
        	tangents = createTangentsArray(mesh);
        }
        
        if (with_uv) {
        	uvs = createUVArray(mesh);
        }
        
        Assimp.aiReleaseImport(scene);
        
        //Create Model Data
        
        List<RawFloatBuffer> float_buffers = new ArrayList<>();
		
        RawFloatBuffer vertexBuffer = new RawFloatBuffer(vertices, 3);
		float_buffers.add(vertexBuffer);
		
		RawFloatBuffer normalsBuffer = new RawFloatBuffer(normals, 3);
		float_buffers.add(normalsBuffer);
		
		if (with_tangents) {
			RawFloatBuffer tangentsBuffer = new RawFloatBuffer(tangents, 3);
			float_buffers.add(tangentsBuffer);			
		}
		
		if (with_uv) {
			RawFloatBuffer uvBuffer = new RawFloatBuffer(uvs, 2);
			float_buffers.add(uvBuffer);	
		}	
		
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
		
		List<RawFloatBuffer> buffers = new ArrayList<>();
		
        RawFloatBuffer vertexBuffer = new RawFloatBuffer(vertices, 2);
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
		
		List<RawFloatBuffer> buffers = new ArrayList<>();
		
        RawFloatBuffer vertexBuffer = new RawFloatBuffer(vertices, 2);
		buffers.add(vertexBuffer);

        RawFloatBuffer uvBuffer = new RawFloatBuffer(uv, 2);
		buffers.add(uvBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
	public static RawModel screenQuad() {
		return quad2D(new Vector2f().zero(), new Vector2f(2.0f, 2.0f));
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
		
		List<RawFloatBuffer> buffers = new ArrayList<>();
		
        RawFloatBuffer vertexBuffer = new RawFloatBuffer(vertices, 3);
		buffers.add(vertexBuffer);
		
		RawFloatBuffer normalsBuffer = new RawFloatBuffer(normals, 3);
		buffers.add(normalsBuffer);
		
		return new RawModel(indices, buffers, new ArrayList<>());
	}
	
}
