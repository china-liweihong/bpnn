package com.jingdianxi.bpnn;

import java.io.*;
import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.*;
import org.joone.net.*;

/**
 * BPNN�࣬ʵ���������ʼ����ѵ�������Եȷ���
 * ����Ϊ0~1֮���double�����飩
 * ���Ϊ0~1֮���double�����飩
 * ʹ�õĽӿڷ������Զ���ִ������
 */
public class BPNNUtil implements NeuralNetListener, Serializable {
	/**
	 * ���к����ڼ�������
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * �������Ա
	 */
	private NeuralNet nnet = null;
	/**
	 * �����籣��·��
	 */
	private String nnetPath = null;

	/**
	 * ��ʼ��������
	 * @param nnetPath ��������·��
	 * @param inputNum �������Ԫ����
	 * @param hiddenNum ���ز���Ԫ����
	 * @param outputNum �������Ԫ����
	 */
	public void initBPNN(String nnetPath, int inputNum, int hiddenNum, int outputNum) {
		/* ����������ı���·�� */
		this.nnetPath = nnetPath;
		/* �½�����Layer���ֱ���Ϊ����㣬���ز㣬����� */
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();
		/* ����ÿ��Layer��������Ԫ���� */
		input.setRows(inputNum);
		hidden.setRows(hiddenNum);
		output.setRows(outputNum);
		/* �½�����ͻ�����������Ӹ��� */
		FullSynapse synapseIH = new FullSynapse();
		FullSynapse synapseHO = new FullSynapse();
		/* ��������-���أ�����-������� */
		input.addOutputSynapse(synapseIH);
		hidden.addInputSynapse(synapseIH);
		hidden.addOutputSynapse(synapseHO);
		output.addInputSynapse(synapseHO);
		/* �½�һ�������磬���������㣬���ز㣬����� */
		nnet = new NeuralNet();
		nnet.addLayer(input, NeuralNet.INPUT_LAYER);
		nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
	}

	/**
	 * ѵ�������磬ʹ�ô����ļ��������initBPNN
	 * @param trainFile ѵ���ļ����·��
	 * @param trainLength ѵ���ļ�����
	 * @param rate ������ѵ���ٶ�
	 * @param momentum ������ѵ������
	 * @param trainCicles ������ѵ������
	 */
	public void trainBPNN(String trainFile, int trainLength, double rate, double momentum, int trainCicles) {
		/* ��ȡ����� */
		Layer input = nnet.getInputLayer();
		/* �½�����ͻ�� */
		FileInputSynapse trains = new FileInputSynapse();
		/* ���������ļ� */
		trains.setInputFile(new File(trainFile));
		/* ����ʹ�õ����� */
		trains.setAdvancedColumnSelector("1,2,3,4");

		/* ��ȡ����� */
		Layer output = nnet.getOutputLayer();
		/* �½�����ͻ�� */
		FileInputSynapse target = new FileInputSynapse();
		/* ���������ļ� */
		target.setInputFile(new File(trainFile));
		/* ����ʹ�õ����� */
		target.setAdvancedColumnSelector("5,6,7,8");

		/* �½�ѵ��ͻ�� */
		TeachingSynapse trainer = new TeachingSynapse();
		/* ����ѵ��Ŀ�� */
		trainer.setDesired(target);

		/* �������������ͻ�� */
		input.addInputSynapse(trains);
		/* ������������ͻ�� */
		output.addOutputSynapse(trainer);
		/* �����������ѵ��ͻ�� */
		nnet.setTeacher(trainer);

		/* ��ȡ������ļ����� */
		Monitor monitor = nnet.getMonitor();
		/* ����ѵ������ */
		monitor.setLearningRate(rate);
		/* ����ѵ������ */
		monitor.setMomentum(momentum);
		/* ���������� */
		monitor.addNeuralNetListener(this);
		/* ����ѵ�����ݸ����������� */
		monitor.setTrainingPatterns(trainLength);
		/* ����ѵ������ */
		monitor.setTotCicles(trainCicles);
		/* ��ѵ��ģʽ */
		monitor.setLearning(true);
		/* ��ʼѵ�� */
		nnet.go();
	}

	/**
	 * ѵ�������磬ʹ���ڴ����ݣ������initBPNN
	 * @param TrainData ѵ����������
	 * @param Rate ������ѵ���ٶ�
	 * @param Momentum ������ѵ������
	 * @param TrainCicles ������ѵ������
	 */
	public void trainBPNN(double[][] trainData, double rate, double momentum, int trainCicles) {
		/* ������������ */
		Layer input = nnet.getInputLayer();
		MemoryInputSynapse trains = new MemoryInputSynapse();
		trains.setInputArray(trainData);
		trains.setAdvancedColumnSelector("1,2,3,4");

		/* ����������� */
		Layer output = nnet.getOutputLayer();
		MemoryInputSynapse target = new MemoryInputSynapse();
		target.setInputArray(trainData);
		target.setAdvancedColumnSelector("5,6,7,8");

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(target);
		input.addInputSynapse(trains);
		output.addOutputSynapse(trainer);
		nnet.setTeacher(trainer);

		Monitor monitor = nnet.getMonitor();
		monitor.setLearningRate(rate);
		monitor.setMomentum(momentum);
		monitor.addNeuralNetListener(this);
		monitor.setTrainingPatterns(trainData.length);
		monitor.setTotCicles(trainCicles);
		monitor.setLearning(true);
		nnet.go();
	}

	/**
	 * ѵ�����������磬ʹ�ô����ļ�
	 * @param nnetPath ��������·��
	 * @param trainFile ѵ���ļ����·��
	 * @param trainLength ѵ���ļ�����
	 * @param rate ������ѵ���ٶ�
	 * @param momentum ������ѵ������
	 * @param trainCicles ������ѵ������
	 */
	public void trainBPNN(String nnetPath, String trainFile, int trainLength, double rate, double momentum,	int trainCicles) {
		// ��ʼ��������ı���·��
		this.nnetPath = nnetPath;
		// ��ȡ�����������
		this.nnet = this.getBPNN(nnetPath);

		Monitor monitor = this.nnet.getMonitor();
		monitor.setLearningRate(rate);
		monitor.setMomentum(momentum);
		monitor.addNeuralNetListener(this);
		monitor.setTrainingPatterns(trainLength);
		monitor.setTotCicles(trainCicles);
		monitor.setLearning(true);
		this.nnet.go();
	}

	/**
	 * �����ѵ�������磬ʹ�ô����ļ�
	 * @param nnetPath ��������·��
	 * @param outFile ���Խ�����·��
	 * @param testFile �����ļ����·��
	 * @param testLength �����ļ�����
	 */
	public void testBPNN(String nnetPath, String outFile, String testFile, int testLength) {
		NeuralNet testBPNN = this.getBPNN(nnetPath);
		if (testBPNN != null) {
			Layer input = testBPNN.getInputLayer();
			/* ��������ļ� */
			FileInputSynapse inputStream = new FileInputSynapse();
			inputStream.setInputFile(new File(testFile));
			inputStream.setAdvancedColumnSelector("1,2,3,4");
			input.removeAllInputs();
			input.addInputSynapse(inputStream);

			Layer output = testBPNN.getOutputLayer();
			/* �������ͻ�� */
			FileOutputSynapse fileOutput = new FileOutputSynapse();
			/* ��������ļ�����·�� */
			fileOutput.setFileName(outFile);
			output.addOutputSynapse(fileOutput);

			Monitor monitor = testBPNN.getMonitor();
			monitor.setTrainingPatterns(testLength);
			monitor.setTotCicles(1);
			/* �ر�ѵ��ģʽ */
			monitor.setLearning(false);

			/* ��ʼ���� */
			testBPNN.go();
			System.out.println("test");
		}
	}

	/**
	 * �����ѵ�������磬ʹ�þ�������
	 * @param nnetPath ��������·��
	 * @param outFile ���Խ�����·��
	 * @param testData ���Ծ���
	 */
	public void testBPNN(String nnetPath, String outFile, double[][] testData) {
		NeuralNet testBPNN = this.getBPNN(nnetPath);
		if (testBPNN != null) {
			Layer input = testBPNN.getInputLayer();
			/* ������Ծ��� */
			MemoryInputSynapse inputStream = new MemoryInputSynapse();
			input.removeAllInputs();
			input.addInputSynapse(inputStream);
			inputStream.setInputArray(testData);
			inputStream.setAdvancedColumnSelector("1,2,3,4");

			Layer output = testBPNN.getOutputLayer();
			FileOutputSynapse fileOutput = new FileOutputSynapse();
			fileOutput.setFileName(outFile);
			output.addOutputSynapse(fileOutput);

			Monitor monitor = testBPNN.getMonitor();
			monitor.setTrainingPatterns(testData.length);
			monitor.setTotCicles(1);
			monitor.setLearning(false);

			testBPNN.go();
			System.out.println("test");
		}
	}

	/**
	 * �����ѵ�������磬ʹ���ڴ����
	 * @param nnetPath ��������·��
	 * @param testData ���Ծ���
	 * @return ���Խ��
	 */
	public int[][] testBPNN(String nnetPath, double[][] testData) {
		NeuralNet testBPNN = this.getBPNN(nnetPath);
		int[][] result = new int[testData.length][2];
		if (testBPNN != null) {
			double[] temp = new double[2];

			Layer input = testBPNN.getInputLayer();
			/* ������Ծ��� */
			MemoryInputSynapse inputStream = new MemoryInputSynapse();
			input.removeAllInputs();
			input.addInputSynapse(inputStream);
			inputStream.setInputArray(testData);
			inputStream.setAdvancedColumnSelector("1,2,3,4");

			Layer output = testBPNN.getOutputLayer();
			MemoryOutputSynapse fileOutput = new MemoryOutputSynapse();
			output.addOutputSynapse(fileOutput);

			Monitor monitor = testBPNN.getMonitor();
			monitor.setTrainingPatterns(testData.length);
			monitor.setTotCicles(1);
			monitor.setLearning(false);
			testBPNN.go();

			for (int i = 0; i < result.length; i++) {
				temp = fileOutput.getNextPattern();
				result[i][0] = temp[0] < 0.5 ? 0 : 1;
				result[i][1] = temp[1] < 0.5 ? 0 : 1;
			}

			System.out.println("test");
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ����������
	 * @param nnetPath ��������·��
	 * @return
	 */
	NeuralNet getBPNN(String nnetPath) {
		NeuralNetLoader loader = new NeuralNetLoader(nnetPath);
		NeuralNet nnet = loader.getNeuralNet();
		return nnet;
	}

	/**
	 * ʵ�ֽӿڵķ���
	 */
	@Override
	public void cicleTerminated(NeuralNetEvent event) {
		/* ��ȡ������ */
		Monitor mon = (Monitor) event.getSource();
		/* ��ȡ��ѵ������ */
		long totalcicles = mon.getTotCicles();
		/* ��ȡ��ǰѵ������ */
		long currentcicle = mon.getCurrentCicle();

		/* ����ѵ������ */
		if (currentcicle == (int) (totalcicles * 0.3)) {
			double rate = mon.getLearningRate();
			mon.setLearningRate(rate * 0.5);
		} else if (currentcicle == (int) (totalcicles * 0.5)) {
			double rate = mon.getLearningRate();
			mon.setLearningRate(rate * 0.5);
		} else if (currentcicle == (int) (totalcicles * 0.8)) {
			double rate = mon.getLearningRate();
			mon.setLearningRate(rate * 0.5);
		}

		/* ��ȡ����� */
		double err = mon.getGlobalError();
		if (currentcicle % 10000 == 0) {
			System.out.println(currentcicle + " epochs remaining - RMSE = " + err);
		}
	}

	@Override
	public void netStarted(NeuralNetEvent event) {
		System.out.println("start");
	}

	@Override
	public void netStopped(NeuralNetEvent event) {
		System.out.println("Training Stopped...");
		Monitor mon = (Monitor) event.getSource();
		double err = mon.getGlobalError();
		System.out.println("Final - RMSE = " + err);
		/* ������ѵ������ */
		try {
			FileOutputStream stream = new FileOutputStream(nnetPath);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			/* д��nnet���� */
			out.writeObject(nnet);
			out.close();
			System.out.println("Save in " + nnetPath);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		/* ����������� */
		NeuralNet nnet = this.getBPNN(nnetPath);
		if (nnet != null) {
			/* get the output layer */
			Layer output = nnet.getOutputLayer();
			/* create an output synapse */
			FileOutputSynapse fileOutput = new FileOutputSynapse();
			long currentTime = System.currentTimeMillis();
			fileOutput.setFileName("res/train_output_" + currentTime + ".txt");
			/* attach the output synapse to the last layer of the NN */
			output.addOutputSynapse(fileOutput);
			// Run the neural network only once (1 cycle) in recall mode
			nnet.getMonitor().setTotCicles(1);
			nnet.getMonitor().setLearning(false);
			nnet.go();
		}
	}

	@Override
	public void errorChanged(NeuralNetEvent event) {

	}

	@Override
	public void netStoppedError(NeuralNetEvent event, String error) {
		System.out.println("Network stopped due the following error: " + error);
	}

}