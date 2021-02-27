#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
    int waterId;
    vec3 color;
} gs_in[];

uniform mat4 g_viewProjection;

out vec3 color;

void main() {
    color = abs(gl_in[0].gl_Position.xyz/10);

    vec4 position = vec4(-0.9, -0.9, 0, 1);
    gl_Position = position;
    EmitVertex();
    gl_Position = position + vec4(0.1, 0, 0, 0);
    EmitVertex();
    gl_Position = position + vec4(0, 0.1, 0, 0);
    EmitVertex();
    EndPrimitive();

    color = max(sign(color), 0);
    position = vec4(0.9, 0.9, 0, 1);
    gl_Position = position;
    EmitVertex();
    gl_Position = position - vec4(0.1, 0, 0, 0);
    EmitVertex();
    gl_Position = position - vec4(0, 0.1, 0, 0);
    EmitVertex();
    EndPrimitive();

    position = gl_in[0].gl_Position;
    gl_Position = g_viewProjection * (position + vec4(0, 1, 0, 0));
    EmitVertex();
    gl_Position = g_viewProjection * (position + vec4(1, 1, 0, 0));
    EmitVertex();
    gl_Position = g_viewProjection * (position + vec4(0, 1, 1, 0));
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * (position + vec4(0, 1, 1, 0));
    EmitVertex();
    gl_Position = g_viewProjection * (position + vec4(1, 1, 1, 0));
    EmitVertex();
    gl_Position = g_viewProjection * (position + vec4(1, 1, 0, 0));
    EmitVertex();
    EndPrimitive();


}