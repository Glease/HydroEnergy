layout (location = 0) in vec3 in_Position;
layout (location = 1) in float light0;
layout (location = 2) in float light1;
layout (location = 3) in float info;
layout (location = 4) in float worldColorModifier;

out VS_OUT {
    int waterId;
    vec3 worldColorModifier;
    vec2 lightXMinus;
    vec2 lightXPlus;
    vec2 lightYMinus;
    vec2 lightYPlus;
    vec2 lightZMinus;
    vec2 lightZPlus;
    int shouldRenderXMinus;
    int shouldRenderXPlus;
    int shouldRenderYMinus;
    int shouldRenderYPlus;
    int shouldRenderZMinus;
    int shouldRenderZPlus;
} vs_out;

vec2 toLightLUTCoord(int lights) {
    return (vec2(lights >> 4, lights & 0xF) + 0.5) / 16.0;
}

void main(void) {
    gl_Position = vec4(in_Position, 1.0);

    int tmp = int(round(light0));
    vs_out.lightXMinus = toLightLUTCoord((tmp >> 16) & 0xFF);
    vs_out.lightXPlus = toLightLUTCoord((tmp >> 8) & 0xFF);
    vs_out.lightYMinus = toLightLUTCoord(tmp & 0xFF);
    tmp = int(round(light1));
    vs_out.lightYPlus = toLightLUTCoord((tmp >> 16) & 0xFF);
    vs_out.lightZMinus = toLightLUTCoord((tmp >> 8) & 0xFF);
    vs_out.lightZPlus = toLightLUTCoord(tmp & 0xFF);

    tmp = int(round(info));
    vs_out.waterId = tmp >> 6;

    vs_out.shouldRenderXMinus = tmp & 0x1;
    vs_out.shouldRenderXPlus = tmp & 0x2;
    vs_out.shouldRenderYMinus = tmp & 0x4;
    vs_out.shouldRenderYPlus = tmp & 0x8;
    vs_out.shouldRenderZMinus = tmp & 0x10;
    vs_out.shouldRenderZPlus = tmp & 0x20;

    tmp = int(round(worldColorModifier));
    vs_out.worldColorModifier = vec3((tmp >> 16) & 0xFF, (tmp >> 8) & 0xFF, tmp & 0xFF) / 255.0;
}