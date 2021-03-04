#version 330 core
// in_Position was bound to attribute index 0 and in_waterID was bound to attribute index 1
layout (location = 0) in vec3 in_Position;
layout (location = 1) in float in_waterId;
layout (location = 2) in vec3 in_worldColorModifier;

out VS_OUT {
    int waterId;
    vec3 worldColorModifier;
} vs_out;

void main(void) {
    gl_Position = vec4(in_Position, 1.0);
    vs_out.waterId = int(round(in_waterId));
    vs_out.worldColorModifier = vec3(1, 0.5, 0.65);
}