#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
    float waterId;
    vec3 worldColorModifier;
} gs_in[];

uniform mat4 g_viewProjection;
uniform float g_waterLevels[NUM_CONTROLLERS];

const vec4 up = vec4(0, 1, 0, 0);
const vec4 right = vec4(1, 0, 0, 0);
const vec4 back = vec4(0, 0, 1, 0);

const float LIGHT_Y_NEG = 0.5;
const float LIGHT_Y_POS = 1.0;
const float LIGHT_XZ_NEG = 0.8;
const float LIGHT_XZ_POS = 0.6;
const float RENDER_OFFSET = 0.0010000000474974513;

out vec3 color;
out vec2 texCoord;
out float blockLight;
out float skyLight;

void drawQuad(vec4 position00, vec2 texCoord00, vec4 position01, vec2 texCoord01, vec4 position10, vec2 texCoord10, vec4 position11, vec2 texCoord11) {
    gl_Position = g_viewProjection * position00;
    texCoord = texCoord00;
    EmitVertex();
    gl_Position = g_viewProjection * position01;
    texCoord = texCoord01;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    texCoord = texCoord10;
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * position01;
    texCoord = texCoord01;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    texCoord = texCoord10;
    EmitVertex();
    gl_Position = g_viewProjection * position11;
    texCoord = texCoord11;
    EmitVertex();
    EndPrimitive();
}

void main() {
    skyLight = 13;
    blockLight = 0;

    int waterId = int(round(gs_in[0].waterId));
    bool shouldRenderXMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderXPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;

    float waterLevel = g_waterLevels[waterId];


    vec4 position = gl_in[0].gl_Position;
    if(shouldRenderXMinus && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_XZ_POS;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x += RENDER_OFFSET;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 0.5);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.0, 0.5);
        vec4 position10 = position + back;
        vec2 texCoord10 = vec2(0.5, height);
        vec4 position11 = position + _up + back;
        vec2 texCoord11 = vec2(0.0, height);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(shouldRenderXPlus && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_XZ_POS;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x -= RENDER_OFFSET;
        position = position + right;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.5);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.5, 0.5);
        vec4 position10 = position + back;
        vec2 texCoord10 = vec2(0.0, height);
        vec4 position11 = position + _up + back;
        vec2 texCoord11 = vec2(0.5, height);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if((shouldRenderYPlus || waterLevel < (position.y + 1)) && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_Y_POS;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.y -= RENDER_OFFSET;
        position = position + _up;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.0);
        vec4 position01 = position + right;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + back;
        vec2 texCoord10 = vec2(1.0, 0.0);
        vec4 position11 = position + right + back;
        vec2 texCoord11 = vec2(1.0, 1.0);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(shouldRenderYMinus && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_Y_NEG;

        position.y += RENDER_OFFSET;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.0);
        vec4 position01 = position + back;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + right;
        vec2 texCoord10 = vec2(1.0, 0.0);
        vec4 position11 = position + back + right;
        vec2 texCoord11 = vec2(1.0, 1.0);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(shouldRenderZMinus && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z += RENDER_OFFSET;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.5);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.0, height);
        vec4 position10 = position + right;
        vec2 texCoord10 = vec2(0.5, 0.5);
        vec4 position11 = position + _up + right;
        vec2 texCoord11 = vec2(0.5, height);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(shouldRenderZPlus && waterLevel >= position.y) {
        color = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z -= RENDER_OFFSET;
        position = position + back;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 0.5);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.5, height);
        vec4 position10 = position + right;
        vec2 texCoord10 = vec2(0.0, 0.5);
        vec4 position11 = position + _up + right;
        vec2 texCoord11 = vec2(0.0, height);
        drawQuad(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }
}