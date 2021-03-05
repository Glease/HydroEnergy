#version 330

in vec3 colorModifier;
in vec2 texCoord;
in vec2 lightCoord;
in float fogCoefficient;

uniform sampler2D g_lightLUT;
uniform sampler2D g_atlasTexture;
uniform vec3 g_fogColor;

out vec4 gl_FragColor;

void main(void) {
    gl_FragColor = texture2D(g_atlasTexture, texCoord);
    gl_FragColor.rgb = gl_FragColor.rgb * colorModifier * texture2D(g_lightLUT, lightCoord).x;
    gl_FragColor.rgb = fogCoefficient * gl_FragColor.rgb + (1.0 - fogCoefficient) * g_fogColor;
}