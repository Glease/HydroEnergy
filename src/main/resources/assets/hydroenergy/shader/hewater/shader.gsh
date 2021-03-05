#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
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
} gs_in[];

uniform mat4 g_viewProjection;
uniform float g_waterLevels[NUM_CONTROLLERS];
uniform vec2 g_texCoordStillMin;
uniform vec2 g_texCoordStillDelta;
uniform vec2 g_texCoordFlowingMin;
uniform vec2 g_texCoordFlowingDelta;
uniform float g_fogDiff;
uniform float g_fogEnd;
uniform float g_fogDensity;
uniform float g_fogModeLinear;
uniform vec3 g_cameraPosition;

const vec4 up = vec4(0, 1, 0, 0);
const vec4 right = vec4(1, 0, 0, 0);
const vec4 back = vec4(0, 0, 1, 0);

const float LIGHT_Y_NEG = 0.5;
const float LIGHT_Y_POS = 1.0;
const float LIGHT_XZ_NEG = 0.8;
const float LIGHT_XZ_POS = 0.6;
const float RENDER_OFFSET = 0.0010000000474974513;

out vec3 colorModifier;
out vec2 texCoord;
out vec2 lightCoord;
out float fogCoefficient;

void setFogCoefficient(vec4 position) {
    float c = length(position.xyz - g_cameraPosition);
    float c_linear = clamp((g_fogEnd - c) / g_fogDiff, 0.0, 1.0);
    float c_exp = clamp(exp(-g_fogDensity * c), 0.0, 1.0);
    fogCoefficient =  g_fogModeLinear * c_linear + (1.0 - g_fogModeLinear) * c_exp;
}

void drawQuadHorizontal(vec4 position00, vec2 texCoord00, vec4 position01, vec2 texCoord01, vec4 position10, vec2 texCoord10, vec4 position11, vec2 texCoord11) {
    gl_Position = g_viewProjection * position00;
    setFogCoefficient(position00);
    texCoord = g_texCoordStillMin + texCoord00 * g_texCoordStillDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    setFogCoefficient(position10);
    texCoord = g_texCoordStillMin + texCoord10 * g_texCoordStillDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position01;
    setFogCoefficient(position01);
    texCoord = g_texCoordStillMin + texCoord01 * g_texCoordStillDelta;
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * position01;
    setFogCoefficient(position01);
    texCoord = g_texCoordStillMin + texCoord01 * g_texCoordStillDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    setFogCoefficient(position10);
    texCoord = g_texCoordStillMin + texCoord10 * g_texCoordStillDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position11;
    setFogCoefficient(position11);
    texCoord = g_texCoordStillMin + texCoord11 * g_texCoordStillDelta;
    EmitVertex();
    EndPrimitive();
}

void drawQuadVertical(vec4 position00, vec2 texCoord00, vec4 position01, vec2 texCoord01, vec4 position10, vec2 texCoord10, vec4 position11, vec2 texCoord11) {
    gl_Position = g_viewProjection * position00;
    setFogCoefficient(position00);
    texCoord = g_texCoordFlowingMin + texCoord00 * g_texCoordFlowingDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    setFogCoefficient(position10);
    texCoord = g_texCoordFlowingMin + texCoord10 * g_texCoordFlowingDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position01;
    setFogCoefficient(position01);
    texCoord = g_texCoordFlowingMin + texCoord01 * g_texCoordFlowingDelta;
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * position01;
    setFogCoefficient(position01);
    texCoord = g_texCoordFlowingMin + texCoord01 * g_texCoordFlowingDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position10;
    setFogCoefficient(position10);
    texCoord = g_texCoordFlowingMin + texCoord10 * g_texCoordFlowingDelta;
    EmitVertex();
    gl_Position = g_viewProjection * position11;
    setFogCoefficient(position11);
    texCoord = g_texCoordFlowingMin + texCoord11 * g_texCoordFlowingDelta;
    EmitVertex();
    EndPrimitive();
}

void main() {

    float waterLevel = g_waterLevels[gs_in[0].waterId];

    vec4 position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderXMinus > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_POS;
        lightCoord = gs_in[0].lightXMinus;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x += RENDER_OFFSET;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 1.0);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.5, 1.0 - height);
        vec4 position10 = position + back;
        vec2 texCoord10 = vec2(0.0, 1.0);
        vec4 position11 = position + _up + back;
        vec2 texCoord11 = vec2(0.0, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderXPlus > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_POS;
        lightCoord = gs_in[0].lightXPlus;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x -= RENDER_OFFSET;
        position = position + right;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 1.0);
        vec4 position01 = position + back;
        vec2 texCoord01 = vec2(0.5, 1.0);
        vec4 position10 = position + _up;
        vec2 texCoord10 = vec2(0.0, 1.0 - height);
        vec4 position11 = position + _up + back;
        vec2 texCoord11 = vec2(0.5, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderYMinus > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_Y_NEG;
        lightCoord = gs_in[0].lightYMinus;

        position.y += RENDER_OFFSET;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.0);
        vec4 position01 = position + back;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + right;
        vec2 texCoord10 = vec2(1.0, 0.0);
        vec4 position11 = position + back + right;
        vec2 texCoord11 = vec2(1.0, 1.0);
        drawQuadHorizontal(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if((gs_in[0].shouldRenderYPlus > 0 || waterLevel < (position.y + 1)) && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_Y_POS;
        lightCoord = gs_in[0].lightYPlus;

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
        drawQuadHorizontal(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderZMinus > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;
        lightCoord = gs_in[0].lightZMinus;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z += RENDER_OFFSET;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 1.0);
        vec4 position01 = position + right;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + _up;
        vec2 texCoord10 = vec2(0.5, 1.0 - height);
        vec4 position11 = position + _up + right;
        vec2 texCoord11 = vec2(0.0, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderZPlus > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;
        lightCoord = gs_in[0].lightZPlus;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z -= RENDER_OFFSET;
        position = position + back;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 1.0);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.0, 1.0 - height);
        vec4 position10 = position + right;
        vec2 texCoord10 = vec2(0.5, 1.0);
        vec4 position11 = position + _up + right;
        vec2 texCoord11 = vec2(0.5, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }
}