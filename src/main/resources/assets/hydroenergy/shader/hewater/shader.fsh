#version 330

in vec3 color;
in vec2 texCoord;
in float blockLight;
in float skyLight;

uniform sampler2D g_lightLUT;

out vec4 gl_FragColor;

float getBrightness() {
    vec2 texCoordDiscrete = (vec2(blockLight, skyLight) + 0.5) / 16.0;
    return texture2D(g_lightLUT, texCoordDiscrete).x;
}

void main(void) {
    gl_FragColor = vec4(1.0, 0.75, 0.79, 1);
    gl_FragColor = vec4(color, 0.5);
    gl_FragColor = vec4(texCoord, 0.0, getBrightness());
}