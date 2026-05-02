#version 400 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aNormal;
layout(location=2) in vec3 aTangent;
layout(location=3) in vec2 aUV;

uniform mat4 camTrans;

out vec3 pos;
out vec2 uv;

void main() {
    vec4 pos4 = camTrans * vec4(aPos, 1.0);
    pos = pos4.xyz / pos4.w;
    
    uv = aUV;

    gl_Position = pos4;
}
