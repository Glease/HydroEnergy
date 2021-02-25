#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 g_viewProjection;

in VS_OUT {
    int waterId;
} gs_in[];

void main() {
    vec4 position = gl_in[0].gl_Position;
    position = vec4(-0.5, -0.5, 0, 1);

    gl_Position = position;
    //gl_Position = gl_Position * g_viewProjection;
    EmitVertex();

    gl_Position = position + vec4(1, 0, 0, 0);
    //gl_Position = gl_Position * g_viewProjection;
    EmitVertex();

    gl_Position = position + vec4(0, 1, 0, 0);
    //gl_Position = gl_in[0].gl_Position * g_viewProjection;
    //gl_Position = gl_Position * g_viewProjection;
    EmitVertex();

    EndPrimitive();
}