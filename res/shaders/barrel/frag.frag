#version 400 core

in vec3 pos;
in vec2 uv;

out vec4 fragColor;

uniform sampler2D texSam0;


void main() {
    vec4 baseColor = texture(texSam0, uv);

    vec3 color_rgb = baseColor.rgb;
    fragColor = vec4(color_rgb, 1.0);
}
