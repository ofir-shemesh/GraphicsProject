#version 400 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aNormal;

uniform mat4 rotationTrans;
uniform mat4 camTrans;

out vec3 pos;
out vec3 normal;

void main() {
    mat4 trans = camTrans;
    
    vec4 pos4 = trans * vec4(aPos, 1.0);
    pos = pos4.xyz / pos4.w;
    
    vec4 normal4 = vec4(aNormal, 1.0);
    normal = normalize(normal4.xyz);
    
    gl_Position = pos4;
}
