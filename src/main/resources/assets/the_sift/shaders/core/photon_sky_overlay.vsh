#version 330

#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    vec4 clip = ProjMat * ModelViewMat * vec4(Position, 1.0);
    // Minecraft 26.2 GlDevice sets ARBClipControl ZERO_TO_ONE and reversed Z.
    // All clear/background pixels are depth zero, and opaque geometry has
    // depth greater than zero. A clip-space Z of -W would be CLIPPED here.
    // Combined with CompareOp.EQUAL, this restricts the overlay to actual
    // sky pixels after Iris has written the FINAL scene to the main target.
    clip.z = 0.0;
    gl_Position = clip;
    texCoord0 = UV0;
}
