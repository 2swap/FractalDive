package com.twoswap.mandelbrot.extras;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import com.twoswap.gui.GUI;
import com.twoswap.mandelbrot.Generator;

public abstract class Mp4 {
	
	public static void makeVideo(String[] filenames, String output) throws IOException {
		SeekableByteChannel out = null;
		try {
		    out = NIOUtils.writableFileChannel(output);
		    AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(12, 1)); // 25fps/1sec
		    for (int i = 0; i < filenames.length; i++) {
		        BufferedImage image = ImageIO.read(new File(filenames[i]));
		        encoder.encodeImage(image);
				GUI.clog("Encoding frame " + (i+1) + "/" + filenames.length);
		    }
		    encoder.finish();
		} finally {
		    NIOUtils.closeQuietly(out);
		}
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
	
}