package com.theishiopian.parrying.Client.Particle;

import com.theishiopian.parrying.Utility.ModUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SliceParticle extends TextureSheetParticle
{
    private final SpriteSet sprites;

    protected SliceParticle(ClientLevel world, double posX, double posY, double posZ, SpriteSet sprites)
    {
        super(world, posX, posY, posZ, 0, 0, 0);
        float f = ModUtil.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 0.35f;
        this.lifetime = 4;
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
    }

    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime)
        {
            this.remove();
        }
        else
        {
            this.setSpriteFromAge(this.sprites);

        }
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public int getLightColor(float level) {
        return 15728880;
    }

    @OnlyIn(Dist.CLIENT)
    public record Factory(SpriteSet sprites) implements ParticleProvider<SimpleParticleType>
    {

        public Particle createParticle(@NotNull SimpleParticleType particle, @NotNull ClientLevel world, double x, double y, double z, double vx, double vy, double vz)
        {
            return new SliceParticle(world, x, y, z, this.sprites);
        }
    }
}
