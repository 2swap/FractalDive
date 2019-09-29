package com.twoswap.mandelbrot.extras;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.twoswap.gui.GUI;
import com.twoswap.mandelbrot.Generator;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;

public class Xugg {

	public static void makeVideo(String[] filenames, String output) throws IOException {
		final IMediaWriter writer = ToolFactory.makeWriter(output);
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(25,1), Generator.width, Generator.height);
		long startTime = System.nanoTime();
		for (int i = 0; i < filenames.length; i++) {
	        BufferedImage screen = ImageIO.read(new File(filenames[i]));
			BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
			writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
		}
		writer.close();
	}

	public static void run() {
		String[] imgs = new String[Generator.time];
		for (int i = 0; i < Generator.time; i++) imgs[i] = "giffer/img" + i + ".png";
		try {
			makeVideo(imgs, "outputVideos/"+System.currentTimeMillis()+".mov");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		GUI.clog("Saved!");
	}

	public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
		BufferedImage image;
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		} else {
			image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}

}