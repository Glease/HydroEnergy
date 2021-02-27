#version 330

in vec3 color;

out vec4 gl_FragColor;

void main(void) {
    gl_FragColor = vec4(1.0, 0.75, 0.79, 1);
    gl_FragColor = vec4(color, 1);
}