#version 400 core

in vec2 pos;

out vec4 fragColor;

uniform vec4 color_top;
uniform vec4 color_bottom;
uniform mat4 camTrans;

uniform samplerCube texSam0;
uniform float starsIntensity;

void main() {
    /*
    float frac = (pos.y+1.0)/2;
    
    vec4 color = frac * color_top + (1.0f - frac) * color_bottom;
    
    fragColor = color;
        */
    
     
     float frac = (pos.y+1.0)/2;
     
     vec4 color = frac * color_top + (1.0f - frac) * color_bottom;
     
     
     vec3 dir = normalize((inverse(camTrans) * vec4(pos, -1.0, 1.0)).xyz);

     vec4 stars_color = vec4(1.0, 1.0, 1.0, 1.0) * texture(texSam0, dir).r * starsIntensity;
     
     fragColor = color + stars_color;
     
     
}
