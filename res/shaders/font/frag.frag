#version 400 core

in vec2 pos;
in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D texSam0;
uniform vec4 color;
uniform vec4 strokeColor;
uniform float thickness;
uniform float strokeThickness;

void main() {
    float dist = 1.0-texture(texSam0, texCoords).a;
    float val = 0.0;
    vec3 final_color = vec3(0.0, 0.0, 0.0);
    if (dist < thickness) {
        val = 1.0;
        final_color = color.rgb;
    }else if (dist < thickness+strokeThickness) {
        val = 1.0;
        final_color = strokeColor.rgb;
    }else{
        val =0.0;
    }

    
    FragColor = vec4(final_color.rgb, val);
}
