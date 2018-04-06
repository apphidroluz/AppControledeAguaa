package br.com.hidroluz.HidroluzWaterControl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ProcessaImagens {

	public static final int MEDIA_TYPE_IMAGE = 1;
	/**
	 * Indica que queremos retornar um URI ou arquivo para o tipo video.
	 */
	public static final int MEDIA_TYPE_VIDEO = 2;

	/**
	 * Checa se o dispositivo tem uma camera.
	 * 
	 * @param context
	 *            O contexto a ser utilizado para verificacao do hardware.
	 * @return True se tiver uma camera, false caso contrario.
	 */
	public static final boolean checarCameraHardware(Context context) {
		// Este dispositivo tem uma camera.
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		// Este dispositivo nao tem uma camera.
		else {
			Log.i("ProcessaImagens", "Este dispositivo não tem uma câmera");
			return false;
		}
	}

	/**
	 * Cria um URI a partir da criacao de um arquivo para salvar uma imagem ou
	 * video.
	 * 
	 * @param type
	 *            Indica se o arquivo e uma imagem ou video.
	 * @param context
	 *            Contexto usado para acessar recursos do sistema e obter o nome
	 *            do app.
	 * @return URI contendo o caminho e nome do arquivo.
	 */
	public static Uri getOutputMediaFileUri(int type, Context context) {
		return Uri.fromFile(getOutputMediaFile(type, context));
	}

	/**
	 * Cria um arquivo para salvar uma imagem ou video
	 * 
	 * @param type
	 * @param context
	 *            Contexto usado para acessar recursos do sistema e obter o nome
	 *            do app.
	 * @return Objeto File para imagem ou video.
	 */
	private static File getOutputMediaFile(int type, Context context) {
		// Obtem o nome do app para usar como o nome da pasta onde as imagens
		// serao salvas dentro da pasta "Pictures"
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = null;

		try {
			applicationInfo = packageManager.getApplicationInfo(
					context.getApplicationInfo().packageName, 0);
		} catch (final PackageManager.NameNotFoundException e) {
		}
		String nomeApp = (String) (applicationInfo != null ? packageManager
				.getApplicationLabel(applicationInfo) : "Desconhecido");

		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// Esta localizacao trabalha melhor se voce quer criar imagens para ser
		// compartilhada entre aplicacoes e persistir depois de seu app ter sido
		// desinstalado.
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Hidroluz");

		// Cria o diretorio se ele nao existe
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(nomeApp,
						"Falha ao criar diretório ou diretório já existe!");
				return null;
			}
		}

		// Cria o nome do arquivo de midia
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HH")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * OBS: Este metodo deve ser executado dentro de uma Thread fora UI.
	 * Responsavel por carregar a miniatura (thumbnail) de uma imagem.
	 * 
	 * @param cr
	 *            ContentResolver usado para despachar consultas para
	 *            MediaProvider.
	 * @param imagePath
	 *            O caminho para a imagem.
	 * @param imageId
	 *            O id da imagem associada com a miniatura de interesse.
	 * @return Um objeto bitmap contento a miniatura
	 */
	public static final Bitmap getMiniaturaImagem(ContentResolver cr,
			String imagePath, long imageId) {
		// Recupera a miniatura da imagem que foi passada o id.
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, imageId,
				MediaStore.Images.Thumbnails.MICRO_KIND,
				new BitmapFactory.Options());

		// Checa a rotacao da imagem e a exibe corretamente.
		ExifInterface exif;
		try {
			exif = new ExifInterface(imagePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Orientação original.: " + orientation);

			// Constr�i uma matriz identidade para rotacionar o bitmap
			// miniatura.
			Matrix matrix = new Matrix();

			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Orientação.: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Orientação.: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Orientação.: " + orientation);
			}

			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (IllegalArgumentException e) {
			Log.e("ProcessaImagens",
					"Se os valores de x, y, largura e altura estão fora das dimensões do bitmap fonte.",
					e);
		} catch (IOException e) {
			Log.e("ProcessaImagens",
					"Erro na leitura das tags Exif do arquivo jpeg especificado.",
					e);
		}

		return bitmap;
	}

	/**
	 * OBS: Este metodo deve ser executado dentro de uma Thread fora UI.
	 * Compacta e da a devida orientacao da imagem. Este metodo deve ser
	 * executado dentro de uma Thread fora UI.
	 * 
	 * @param imagePath
	 *            O caminho para a imagem a ser compactada.
	 * @return Uma Lista contendo o objeto Bitmap compactado na primeira posicao
	 *         (0) e seu array de bytes na segunda posicao (1).
	 */
	public static final List<Object> compactarImagem(String imagePath) {
		// Lista de objetos que sera retornado pelo metodo.
		List<Object> listReturned = new ArrayList<Object>();

		Bitmap scaledBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();

		// Definindo este campo como true, os pixels reais do bitmap nao sao
		// carregados na memoria. Apenas os limites sao carregados. Se
		// voce tentar usar o bitmap aqui, voc� ira obter nulo.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

		// Os valores maximo de altura e largura da imagem comprimida sao em
		// torno de 816x612
		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		// Valores de largura e altura sao definidas mantendo a relacao de
		// aspecto da imagem
		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;
			}
		}

		// Definindo o valor de inSampleSize permite carregar uma versao
		// reduzida da imagem original.
		options.inSampleSize = calculateInSampleSize(options, actualWidth,
				actualHeight);

		// inJustDecodeBounds definida como false para carregar o bitmap real.
		options.inJustDecodeBounds = false;

		// Esta opcao permite que android reivindique a memeria do bitmap se ele
		// e executado com pouca memoria.
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			// Carrega o bitmap.
			bmp = BitmapFactory.decodeFile(imagePath, options);
		} catch (OutOfMemoryError exception) {
			Log.e("ProcessaImagens",
					"Estouro de memória ao tentar decodificar caminho de arquivo para bitmap.",
					exception);
		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			Log.e("ProcessaImagens",
					"Estouro de memória ao tentar criar bitmap escalado.",
					exception);
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
				middleY - bmp.getHeight() / 2, new Paint(
						Paint.FILTER_BITMAP_FLAG));

		// Checa a rotacao da imagem e a exibe corretamente.
		ExifInterface exif;
		try {
			exif = new ExifInterface(imagePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);

			Matrix matrix = new Matrix();

			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}

			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
		} catch (IllegalArgumentException e) {
			Log.e("ProcessaImagens",
					"Se os valores de x, y, largura e altura estão fora das dimensões do bitmap fonte.",
					e);
		} catch (IOException e) {
			Log.e("ProcessaImagens",
					"Erro na leitura das tags Exif do arquivo jpeg especificado.",
					e);
		}

		// Um OutputStream de array de bytes para escrever array de bytes nele.
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// (outputStream) especificado.
		scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

		// Adiciona os objetos a lista.
		listReturned.add(scaledBitmap);
		listReturned.add(outputStream.toByteArray());

		return listReturned;
	}

	/**
	 * Calcula um valor adequado para inSampleSize com base nas dimensoes reais
	 * e necessarias.
	 * 
	 * @param options
	 *            Opcoes para objeto Bitmap.
	 * @param reqWidth
	 *            Valor real da largura.
	 * @param reqHeight
	 *            Valor real da altura.
	 * @return Valor adequado para inSampleSize
	 */
	private static final int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}
}
