#version 400 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec3 aNormal;

uniform mat4 camTrans;
uniform mat4 camTranslationTrans;

out vec2 posOnFloor;

void main() {
    mat4 trans = camTrans;
    posOnFloor = aPos.xz;
    vec4 pos4 = trans * vec4(aPos, 1.0);

    gl_Position = pos4;
}
