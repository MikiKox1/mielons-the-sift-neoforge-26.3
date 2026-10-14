#version 330

#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    vec4 clip = ProjMat * ModelViewMat * vec4(Position, 1.0);
    // MC26.1.2 uses the OpenGL forward-depth path (not MC26.2 reversed-Z).
    // A clip Z of +W maps to the far depth = 1 where main depth was cleared.
    // CompareOp.EQUAL with writes disabled limits Photon post-final sky
    // to background pixels, leaving nearer terrain and blocks unobstructed.
    // The actual Iris/Photon final target depth still needs in-world QA.
    clip.z = clip.w;
    gl_Position = clip;
    texCoord0 = UV0;
}
