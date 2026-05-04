#version 400 core

in vec3 pos;
in vec3 normal;

uniform vec3 lightDir;
out vec4 fragColor;

void main() {
    vec3 lightDirection = lightDir;

    float dot_prod = dot(-lightDirection, normal);
    float value = clamp(0.2+abs(dot_prod), 0.5, 0.9);
    
    vec3 color_rgb = vec3(0.3, 0.5, 0.2) * value;
    fragColor = vec4(color_rgb, 1.0);
}
