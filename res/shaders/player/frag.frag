#version 400 core

in vec3 pos;
in vec3 normal;

out vec4 fragColor;

void main() {
    vec3 lightDir = vec3(0.0, 1.0, 0.0);
    
    float dot_prod = dot(-lightDir, normal);
    float value = clamp(0.2+abs(dot_prod), 0.5, 0.9);
    
    vec3 color_rgb = vec3(0.3, 0.5, 0.2) * value;
    fragColor = vec4(color_rgb, 1.0);
}
