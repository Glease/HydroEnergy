layout (location = 0) in vec3 in_Position;
layout (location = 1) in float light0;
layout (location = 2) in float light1;
layout (location = 3) in float info;
layout (location = 4) in float worldColorModifier;

out VS_OUT {
    int waterId;
    vec3 worldColorModifier;
    vec2 lightWest;
    vec2 lightEast;
    vec2 lightDown;
    vec2 lightUp;
    vec2 lightNorth;
    vec2 lightSouth;
    int shouldRenderWest;
    int shouldRenderEast;
    int shouldRenderDown;
    int shouldRenderUp;
    int shouldRenderNorth;
    int shouldRenderSouth;
} vs_out;

vec2 toLightLUTCoord(int lights) {
    return (vec2(lights >> 4, lights & 0xF) + 0.5) / 16.0;
}

void main(void) {
    gl_Position = vec4(in_Position, 1.0);

    int tmp = int(round(light0));
    vs_out.lightWest = toLightLUTCoord((tmp >> 16) & 0xFF);
    vs_out.lightEast = toLightLUTCoord((tmp >> 8) & 0xFF);
    vs_out.lightDown = toLightLUTCoord(tmp & 0xFF);
    tmp = int(round(light1));
    vs_out.lightUp = toLightLUTCoord((tmp >> 16) & 0xFF);
    vs_out.lightNorth = toLightLUTCoord((tmp >> 8) & 0xFF);
    vs_out.lightSouth = toLightLUTCoord(tmp & 0xFF);

    tmp = int(round(info));
    vs_out.waterId = tmp >> 6;

    vs_out.shouldRenderWest = tmp & 0x1;
    vs_out.shouldRenderEast = tmp & 0x2;
    vs_out.shouldRenderDown = tmp & 0x4;
    vs_out.shouldRenderUp = tmp & 0x8;
    vs_out.shouldRenderNorth = tmp & 0x10;
    vs_out.shouldRenderSouth = tmp & 0x20;

    tmp = int(round(worldColorModifier));
    vs_out.worldColorModifier = vec3((tmp >> 16) & 0xFF, (tmp >> 8) & 0xFF, tmp & 0xFF) / 255.0;
}