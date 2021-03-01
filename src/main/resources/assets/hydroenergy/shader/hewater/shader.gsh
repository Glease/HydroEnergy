#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
    float waterId;
    vec3 worldColorModifier;
} gs_in[];

uniform mat4 g_viewProjection;

const vec4 up = vec4(0, 1, 0, 0);
const vec4 right = vec4(1, 0, 0, 0);
const vec4 back = vec4(0, 0, 1, 0);

out vec3 color;

void main() {
    color = gs_in[0].worldColorModifier;
    vec4 position = gl_in[0].gl_Position;

    int waterId = int(round(gs_in[0].waterId));
    bool shouldRenderXPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderXMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;

    if(shouldRenderXMinus) {
        float height = 1;
        //float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = height * up;
        color = vec3(1.0, 0.0, 0.0);
        // TODO: do color stuff
        gl_Position = g_viewProjection * position;
        EmitVertex();
        gl_Position = g_viewProjection * (position + _up);
        EmitVertex();
        gl_Position = g_viewProjection * (position + back);
        EmitVertex();
        EndPrimitive();

        gl_Position = g_viewProjection * (position + _up);
        EmitVertex();
        gl_Position = g_viewProjection * (position + _up + back);
        EmitVertex();
        gl_Position = g_viewProjection * (position + back);
        EmitVertex();
        EndPrimitive();
    }


    color = vec3(0,0,1);
    gl_Position = g_viewProjection * (position + up);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + back);
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * (position + up + back);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right + back);
    EmitVertex();
    EndPrimitive();
}