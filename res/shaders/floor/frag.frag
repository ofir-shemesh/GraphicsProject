#version 400 core

in vec2 posOnFloor;

out vec4 fragColor;

void main() {
    int x = int(floor(posOnFloor.x));
    int y = int(floor(posOnFloor.y));
    
    vec3 color_rgb = vec3(0.7, 0.3, 0.0);
    
    if ((x+y) % 2 == 0)
    {
        color_rgb = vec3(0.0, 0.3, 0.7);
    }
    fragColor = vec4(color_rgb, 1.0);
}
