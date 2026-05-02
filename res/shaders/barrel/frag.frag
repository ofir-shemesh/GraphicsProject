#version 400 core

in vec3 pos;
in vec2 uv;

in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

out vec4 fragColor;

uniform sampler2D texSam0;
uniform sampler2D texSam1;


void main() {
    vec3 lightDir = vec3(0.0, 1.0, 1.0);
    lightDir = normalize(lightDir);

    vec4 baseColor = texture(texSam0, uv);
    vec3 relativeNormalCoords = texture(texSam1, uv).rgb * 2.0 - 1.0;

    vec3 effectiveNormal = tangent * relativeNormalCoords.r +
                           bitangent * relativeNormalCoords.g +
                           normal * relativeNormalCoords.b;
    
    effectiveNormal = normalize(effectiveNormal);
    
    float val = clamp(abs(dot(effectiveNormal, normalize(lightDir))), 0.0, 1.0);
    fragColor = vec4(val*baseColor.rgb, 1.0);
}
