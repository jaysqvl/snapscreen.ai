export default function DashboardPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-6">
      <div className="w-full max-w-4xl space-y-6">
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-lg text-gray-600">
          Welcome to SnapScreen! Your authentication was successful.
        </p>
        <p>
          You can now start scanning your resume against job descriptions.
        </p>
      </div>
    </div>
  )
} 