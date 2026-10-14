#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    // MC26.1.2 uses forward-Z. When the dome's projected clip.z == clip.w,
    // perspective interpolation can round the resulting far-depth slightly
    // differently for neighbouring triangles. Under CompareOp.EQUAL that
    // turns a smooth sky into a patchwork of visible/missing polygons.
    // Force the exact background clear depth per fragment instead, while the
    // render pipeline keeps depth writes disabled and tests EQUAL so terrain
    // remains in front. The accepted MC26.2 reversed-Z shader is untouched.
    gl_FragDepth = 1.0;
    vec4 sampled = texture(Sampler0, texCoord0);
    if (sampled.a <= 0.001) {
        discard;
    }
    fragColor = sampled * ColorModulator;
}
