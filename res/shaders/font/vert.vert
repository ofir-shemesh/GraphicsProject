#version 400 core
layout(location=0) in vec2 aPos;
layout(location=1) in vec2 uv;

out vec2 pos;
out vec2 texCoords;

void main() {
    pos = aPos;
    texCoords = uv;
    
    gl_Position = vec4(aPos, 0.0, 1.0);
}
