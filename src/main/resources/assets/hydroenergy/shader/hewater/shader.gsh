#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 3) out;

in VS_OUT {
    int waterId;
} gs_in[];

void main() {
    vec4 position = gl_in[0].gl_Position;

    gl_Position = position;
    EmitVertex();

    gl_Position = position + vec4(-0.1, 0.0, 0.0, 0.0);
    EmitVertex();

    gl_Position = position + vec4( 0.1, 0.0, 0.0, 0.0);
    EmitVertex();

    EndPrimitive();
}