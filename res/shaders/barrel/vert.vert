#version 400 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aNormal;
layout(location=2) in vec3 aTangent;
layout(location=3) in vec2 aUV;

uniform mat4 camTrans;

out vec3 pos;
out vec2 uv;

out vec3 normal;
out vec3 tangent;
out vec3 bitangent;


void main() {
    vec4 pos4 = camTrans * vec4(aPos, 1.0);
    pos = pos4.xyz / pos4.w;
    
    
    vec4 normal4 = vec4(aNormal, 1.0);
    normal = normalize(normal4.xyz);
    
    vec4 tangent4 = vec4(aTangent, 1.0);
    tangent = normalize(tangent4.xyz);
    tangent = normalize(tangent - normal * dot(normal, tangent));
    
    bitangent = normalize(cross(normal, tangent));
    
    
    uv = aUV;

    gl_Position = pos4;
}
