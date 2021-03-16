layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
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
} gs_in[];

uniform mat4 g_viewProjection;
uniform float g_waterLevels[NUM_CONTROLLERS];
uniform float g_debugModes[NUM_CONTROLLERS];
uniform vec2 g_texCoordStillMin;
uniform vec2 g_texCoordStillDelta;
uniform vec2 g_texCoordFlowingMin;
uniform vec2 g_texCoordFlowingDelta;
uniform float g_fogDiff;
uniform float g_fogEnd;
uniform float g_fogDensity;
uniform float g_fogModeLinear;
uniform vec3 g_cameraPosition;
uniform float g_renderOffset;

const vec4 up = vec4(0, 1, 0, 0);
const vec4 east = vec4(1, 0, 0, 0);
const vec4 south = vec4(0, 0, 1, 0);

const float LIGHT_Y_NEG = 0.5;
const float LIGHT_Y_POS = 1.0;
const float LIGHT_XZ_NEG = 0.8;
const float LIGHT_XZ_POS = 0.6;

out vec3 colorModifier;
out vec2 texCoord;
out vec2 lightCoord;
out float fogCoefficient;
out float debugSwitch;

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
    debugSwitch = g_debugModes[gs_in[0].waterId];

    float waterLevel = g_waterLevels[gs_in[0].waterId];

    vec4 position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderWest > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_POS;
        lightCoord = gs_in[0].lightWest;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x += g_renderOffset;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 1.0);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.5, 1.0 - height);
        vec4 position10 = position + south;
        vec2 texCoord10 = vec2(0.0, 1.0);
        vec4 position11 = position + _up + south;
        vec2 texCoord11 = vec2(0.0, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderEast > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_POS;
        lightCoord = gs_in[0].lightEast;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.x -= g_renderOffset;
        position = position + east;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 1.0);
        vec4 position01 = position + south;
        vec2 texCoord01 = vec2(0.5, 1.0);
        vec4 position10 = position + _up;
        vec2 texCoord10 = vec2(0.0, 1.0 - height);
        vec4 position11 = position + _up + south;
        vec2 texCoord11 = vec2(0.5, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    // Vanilla water does not render the lower side of a water block if it touches eg. glass. Uncommend for nicer water
    /*position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderDown > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_Y_NEG;
        lightCoord = gs_in[0].lightDown;

        position.y += g_renderOffset;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 0.0);
        vec4 position01 = position + south;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + east;
        vec2 texCoord10 = vec2(1.0, 0.0);
        vec4 position11 = position + south + east;
        vec2 texCoord11 = vec2(1.0, 1.0);
        drawQuadHorizontal(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }*/

    position = gl_in[0].gl_Position;
    if(((gs_in[0].shouldRenderUp > 0 || waterLevel < (position.y + 1)) && waterLevel >= position.y) && !(gs_in[0].shouldRenderDown > 0 && waterLevel <= position.y + CLIPPING_OFFSET)) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_Y_POS;
        lightCoord = gs_in[0].lightUp;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.y -= g_renderOffset;
        position = position + _up;

        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 1.0);
        vec4 position01 = position + east;
        vec2 texCoord01 = vec2(1.0, 1.0);
        vec4 position10 = position + south;
        vec2 texCoord10 = vec2(0.0, 0.0);
        vec4 position11 = position + east + south;
        vec2 texCoord11 = vec2(1.0, 0.0);
        drawQuadHorizontal(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderNorth > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;
        lightCoord = gs_in[0].lightNorth;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z += g_renderOffset;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.5, 1.0);
        vec4 position01 = position + east;
        vec2 texCoord01 = vec2(0.0, 1.0);
        vec4 position10 = position + _up;
        vec2 texCoord10 = vec2(0.5, 1.0 - height);
        vec4 position11 = position + _up + east;
        vec2 texCoord11 = vec2(0.0, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }

    position = gl_in[0].gl_Position;
    if(gs_in[0].shouldRenderSouth > 0 && waterLevel >= position.y) {
        colorModifier = gs_in[0].worldColorModifier * LIGHT_XZ_NEG;
        lightCoord = gs_in[0].lightSouth;

        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = vec4(0.0, height, 0.0, 0.0);
        position.z -= g_renderOffset;
        position = position + south;

        height = height * 0.5;
        vec4 position00 = position;
        vec2 texCoord00 = vec2(0.0, 1.0);
        vec4 position01 = position + _up;
        vec2 texCoord01 = vec2(0.0, 1.0 - height);
        vec4 position10 = position + east;
        vec2 texCoord10 = vec2(0.5, 1.0);
        vec4 position11 = position + _up + east;
        vec2 texCoord11 = vec2(0.5, 1.0 - height);
        drawQuadVertical(position00, texCoord00, position01, texCoord01, position10, texCoord10, position11, texCoord11);
    }
}