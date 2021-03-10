in vec3 colorModifier;
in vec2 texCoord;
in vec2 lightCoord;
in float fogCoefficient;
in float debugSwitch;

uniform sampler2D g_lightLUT;
uniform sampler2D g_atlasTexture;
uniform vec3 g_fogColor;

const vec3 debugColor = vec3(1.0, 0.7, 0.5);

out vec4 gl_FragColor;

void main(void) {
    gl_FragColor = texture2D(g_atlasTexture, texCoord);
    float light = texture2D(g_lightLUT, lightCoord).x;
    gl_FragColor.rgb = gl_FragColor.rgb * colorModifier * light;
    gl_FragColor.rgb = fogCoefficient * gl_FragColor.rgb + (1.0 - fogCoefficient) * g_fogColor;
    if(debugSwitch > 0.5)
        gl_FragColor = vec4(debugSwitch * light * debugColor, 0.65);
}