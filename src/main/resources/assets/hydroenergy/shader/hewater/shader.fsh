#version 330

in vec3 colorModifier;
in vec2 texCoord;
in vec2 lightCoord;

uniform sampler2D g_lightLUT;
uniform sampler2D g_atlasTexture;

out vec4 gl_FragColor;

void main(void) {
    gl_FragColor = texture2D(g_atlasTexture, texCoord) * vec4(colorModifier, 1.0) * texture2D(g_lightLUT, lightCoord).x;
}